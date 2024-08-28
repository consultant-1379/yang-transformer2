package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

public class ReadMe {
	/*
	Welcome to YT2. To understand this code base, and the comments / descriptions throughout the code, you MUST
	have a detailed knowledge of the YANG modeling language, and EModel.

	There are two fundamental principles that YT2 adheres to:

	1.) The model generated for a given YAM is *ALWAYS* the same, irrespective of any augmentation or deviation.
	    This means if there are two different NRMs, with different augmentations and deviations, that the
	    non-version-specific models generated as 100% the same. (There are some error cases where this cannot
	    be adhered to, which are discussed below.)

	    This results in a complete de-coupling between different node types, and different versions of the same
	    node type, such that their builds cannot interfere. In other words, a "bad" model for node type ABC will
	    never interfere with node type XYZ.

	2.) Any delta to a YAM (as result of an augmentation or deviation) will *ALWAYS* be generated into a target
	    version-specific extension model. Inside the extension, model elements are added, removed or replaced as
	    appropriate.

	To help with the above, YT2 will generate into memory three different variants of the schema:

	1.) The BASE variant, which does not merge-in any augmentations or deviation. In effect, the BASE variant
	    represents the contents of the YAMs as they were originally intended by the designer of the YAM.
	2.) The AUGMENTED variant, which only merges-in augmentations, but not deviations. The AUGMENTED variant
	    represents the complete tree before any deviations are applied.
	3.) The DEVIATED variant, which merges-in both augmentations and deviations. The DEVIATED variant represents
	    the schema tree as it is in effect on the node.

	It is worth highlighting that YANG augmentations can only ever add schema nodes (e.g. 'case', 'leaf') but never
	remove schema nodes; likewise, extensions cannot be injected by means of an 'augment'.

	The code and comments will frequently refer to these three variants. Sometimes, the term "ORIGINAL" is used,
	which refers to either BASE or AUGMENTED. ORIGINAL is used when it makes no difference whether the code logic
	operates on BASE or AUGMENTED, typically because whatever the code is interested-in is represented in the exact
	same way in both BASE and AUGMENTED.

	Submodules
	==========

	Submodules pose a significant problem. In effect, the contents of a submodule are always merged with its module;
	that's how YANG works. But there is nothing in the RFC that stipulates that the revision of a module must be
	stepped when one of its submodules changes. So in two different version of an NRM, the very same module (by name
	and revision) could include two different revisions of a submodule. This would lead to unstable generation of
	the module. YT2 solves this (mostly) by considering content coming from a submodule to be of a "foreign" YAM,
	even if the complete definition is within the submodule. So, eg., a MOC defined in a submodule, will be
	generated such to place all attributes (except the key) into the extension.

	Technical debt, bugs etc.
	=========================

	There are a number of TODOs throughout the code and in tests, that refer to various pieces of technical debt etc.
	These are as follows:

	TODO: [ACTION_PARAM_NAMESPACE] - EModel currently does not support namespaces on 'action' (or 'rpc') parameters.
	This would only ever be an issue if a YAM augments an existing action with another leaf/leaf-list, which has
	never been seen in the wild. However, it cannot be ruled out, and should be implemented at some point.

	TODO: [BINARY_DATA_TYPE] - The binary type is presently poorly supported in ENM. Usage of binary type is rare
	in YANG, especially as part of configuration data. We have never seen it as part of an NRM. At present, we
	simply treat binary data as base64-encoded string (that is its canonical representation over NETCONF). We may,
	at some point in the future, have to support full end-to-end support for binary data in ENM. There is no MR for
	this in the backlog at present.

	TODO: [COMBINED_EDT] - A new EModel schema oss_edt_combined has been introduced in late 2021. This schema can be
	used to aggregate all EDTs of the same namespace and version. Usage of combined EDTs dramatically reduces the
	number of models produced by the transformers, and therefore the number of models deployed. At the time of writing
	of YT2, support for combined EDTs was not ready yet in Model Service. However, YT2 code has been prepared for
	combined EDTs, and will do so if so desired. Once MS has been updated, the relevant todo sections should be updated
	accordingly to generate combined EDTs by default. The EPIC is TORF-535933.

	TODO: [MS_BUG_DUPLICATE_ATTRIBUTES_IN_SAME_EXTENSION] - There is currently a bug in Model Service where, during
	the merge of an extension, any duplicate attributes within the same extension model are in effect ignored. So if
	there is an extension with two attributes, same name but different namespace, only one of them is actually merged
	in. The fix for this is captured in TORF-563034. A discussion must be had on whether we should really need to
	support this scenario in Model Service - as part of the duplicate identities MR TORF-536546 we have ruled out
	attribute duplication and implemented code logic inside the YANG Validator to remove duplicates, so really we
	should never see this scenario.

	TODO: [STRUCT_IN_STRUCT] - ENM does not support struct-in-struct. What is supported as data type for an MO
	attribute is a list-of-struct ("struct sequence") or a struct containing a list of scalar types. When generating
	for 3GPP, YT2 will remove any second-level struct (so, remove any list/container data nodes from a struct).
*/
}
