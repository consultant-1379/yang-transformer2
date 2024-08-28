package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.util.XmlGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.parser.ParserExecutionContext;
import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.FindingsManager;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ModifyableFindingSeverityCalculator;
import com.ericsson.oss.mediation.modeling.yangtools.parser.input.YangInput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;

public class NetYangHelper {

	/**
	 * Copies over all YANG modules that were in the input (the YAMs used for transformation, or the original YAMs). Note that
	 * excluded modules (based on namespace) are also copied over, so they are present in Model Service if another node wants these.
	 */
	public static void copyYamsToOutput(final TransformerContext context, final YangDeviceModel deviatedVariant) throws IOException {

		if(context.getOriginalImplementingYangInput().isEmpty()) {
			copyYamsFromSuppliedToOutput(context, deviatedVariant);
		} else {
			copyYamsFromOriginalToOutput(context);
		}
	}

	private static void copyYamsFromSuppliedToOutput(final TransformerContext context, final YangDeviceModel deviatedVariant) throws IOException {

		final List<YangModel> allYams = deviatedVariant.getTopLevelSchema().getModuleRegistry().getAllYangModels();

		for(final YangModel yangModel : allYams) {
			final ModelInfo yangModelInfo = getNetYangModelInfoForYangModel(yangModel);
			final File outFile = XmlGenerator.getFileForModel(context.getOutDir(), yangModelInfo.getSchema(), yangModelInfo.getNamespace(), yangModelInfo.getName(), null);
			copyYamContents(yangModel.getYangInput(), outFile);
		}
	}

	private static void copyYamsFromOriginalToOutput(final TransformerContext context) throws IOException {

		/*
		 * We copy the YAMs from the ORIGINAL paths to the output.
		 * 
		 * We need to read-in the files and build a schema so that we get the module registry.
		 * We will suppress all findings and immediately stop after the initial parse phase as
		 * merging of typedefs etc. is not required, and is only likely to run into problems.
		 */
		final List<YangModel> yangModelFiles = new ArrayList<>();
		context.getOriginalImplementingYangInput().forEach(yangInput -> yangModelFiles.add(new YangModel(yangInput, ConformanceType.IMPLEMENT)));
		context.getOriginalImportingYangInput().forEach(yangInput -> yangModelFiles.add(new YangModel(yangInput, ConformanceType.IMPORT)));

		final ModifyableFindingSeverityCalculator severityCalculator = new ModifyableFindingSeverityCalculator();
		final FindingsManager findingsManager = new FindingsManager(severityCalculator);
		findingsManager.setSuppressAll(true);

		final ParserExecutionContext parserContext = new ParserExecutionContext(findingsManager);

		parserContext.setFailFast(false);
		parserContext.setStopAfterInitialParse(true);

		final YangDeviceModel yangDeviceModel = new YangDeviceModel("Parsed modules");
		yangDeviceModel.parseIntoYangModels(parserContext, yangModelFiles);

		/*
		 * Now we can iterate over the parsed modules and extract the module name etc. One thing to keep in mind
		 * is that the PreProcessor modifies the name of any IMS module / namespace. Since we don't invoke the
		 * PreProcessor here, we need to do the same thing.
		 */
		final List<YangModel> allYams = yangDeviceModel.getTopLevelSchema().getModuleRegistry().getAllYangModels();

		for(final YangModel yangModel : allYams) {

			final String yamNamespace = yangModel.getYangModelRoot().getNamespace();
			final String yamName = yangModel.getModuleIdentity().getModuleName();
			final String yamRevision = yangModel.getModuleIdentity().getRevision();

			final String yamNamespaceCleaned = YangNameVersionUtil.removeNodeAppInstanceName(yamNamespace);
			final String yamNameCleaned = YangNameVersionUtil.removeNodeAppInstanceName(yamName);

			final ModelInfo yangModelInfo = YangNameVersionUtil.getNetYangModelInfoForYam(yamNamespaceCleaned, yamNameCleaned, yamRevision);
			final File outFile = XmlGenerator.getFileForModel(context.getOutDir(), yangModelInfo.getSchema(), yangModelInfo.getNamespace(), yangModelInfo.getName(), null);
			copyYamContents(yangModel.getYangInput(), outFile);
		}
	}

	public static void copyYamContents(final YangInput yangInput, final File outFile) throws IOException {

		outFile.getParentFile().mkdirs();
		Files.deleteIfExists(outFile.toPath());
		Files.createFile(outFile.toPath());

		/*
		 * It is conceivable that at some stage the input we receive is not file-based but purely
		 * stream-based. In this case, copying files does not work of course - so we simply stream
		 * out the contents.
		 */
		try (final InputStream inputStream = yangInput.getInputStream(); final FileOutputStream outputStream = new FileOutputStream(outFile)) {
			final byte[] buf = new byte[20000];
			int length;
			while ((length = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, length);
			}
		}
	}

	/**
	 * Returns the ModelInfo for a YANG model input (i.e. a YAM).
	 * <p>
	 * The namespace and the name of the module are cleaned of the app instance name, if any, and so
	 * stipulated through the context.
	 */
	public static ModelInfo getNetYangModelInfoForYangModel(final YangModel yangModel) {

		final String yamNamespace = PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(yangModel.getYangModelRoot());
		final String yamName = PreProcessor.getModuleNameCleanedOfNodeAppInstanceName(yangModel.getYangModelRoot());
		final String yamRevision = yangModel.getModuleIdentity().getRevision();

		return YangNameVersionUtil.getNetYangModelInfoForYam(yamNamespace, yamName, yamRevision);
	}
}
