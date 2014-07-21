package com.mentor.nucleus.bp.model.compare.test;

import java.util.List;

import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;

import com.mentor.nucleus.bp.core.Association_c;
import com.mentor.nucleus.bp.core.AttributeReferenceInClass_c;
import com.mentor.nucleus.bp.core.Attribute_c;
import com.mentor.nucleus.bp.core.ClassIdentifierAttribute_c;
import com.mentor.nucleus.bp.core.ClassIdentifier_c;
import com.mentor.nucleus.bp.core.CorePlugin;
import com.mentor.nucleus.bp.core.Interface_c;
import com.mentor.nucleus.bp.core.ModelClass_c;
import com.mentor.nucleus.bp.core.Package_c;
import com.mentor.nucleus.bp.core.PackageableElement_c;
import com.mentor.nucleus.bp.core.ReferentialAttribute_c;
import com.mentor.nucleus.bp.core.ReferredToClassInAssoc_c;
import com.mentor.nucleus.bp.core.ReferredToIdentifierAttribute_c;
import com.mentor.nucleus.bp.core.common.BridgePointPreferencesStore;
import com.mentor.nucleus.bp.core.common.ClassQueryInterface_c;
import com.mentor.nucleus.bp.core.ui.DeleteAction;
import com.mentor.nucleus.bp.core.ui.NewAttributeOnO_OBJAction;
import com.mentor.nucleus.bp.core.ui.Selection;
import com.mentor.nucleus.bp.model.compare.TreeDifference;
import com.mentor.nucleus.bp.test.common.BaseTest;
import com.mentor.nucleus.bp.test.common.CompareTestUtilities;
import com.mentor.nucleus.bp.test.common.GitUtil;
import com.mentor.nucleus.bp.test.common.ZipUtil;
import com.mentor.nucleus.bp.ui.explorer.ModelContentProvider;
import com.mentor.nucleus.bp.ui.explorer.ModelLabelProvider;

public class ModelMergeTests2  extends BaseTest {
	private String test_repositories = Platform.getInstanceLocation().getURL()
			.getFile()
			+ "/" + "test_repositories";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mentor.nucleus.bp.test.common.BaseTest#initialSetup()
	 */
	@Override
	protected void initialSetup() throws Exception {
		CorePlugin
				.getDefault()
				.getPreferenceStore()
				.setValue(
						BridgePointPreferencesStore.ENABLE_ERROR_FOR_EMPTY_SYNCHRONOUS_MESSAGE,
						false);
		String test_repository_location = System
				.getenv("XTUML_TEST_MODEL_REPOSITORY");
		if (test_repository_location == null
				|| test_repository_location.equals("")) {
			// use the default location
			test_repository_location = BaseTest.DEFAULT_XTUML_TEST_MODEL_REPOSITORY;
		}
		test_repository_location = new Path(test_repository_location)
				.removeLastSegments(1).toString();
		ZipUtil.unzipFileContents(
				test_repository_location + "/"
						+ "test_repositories"
						+ "/" + "236-merge.zip",
				test_repositories);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors(false);
		BaseTest.dispatchEvents(0);
	}

	public void testMergeInterfaceSortedElements() throws Exception {
		String projectName = "GPS Watch";
		// import git repository from models repo
		GitUtil.loadRepository(test_repositories
				+ "/" + "sandbox", "slave1");
		// import test project
		GitUtil.loadProject(projectName, "sandbox", "slave1");
		// merge the test branch
		GitUtil.mergeBranch("master1", "sandbox", "slave1");
		// start the merge tool
		GitUtil.startMergeTool(projectName);

		// Merge Pkg1 (6 incoming changes in master, 5 outgoing changes from slave)
		CompareTestUtilities.copyAllNonConflictingChangesFromRightToLeft();
		// validate
		assertTrue("Found conflicting changes remaining.", CompareTestUtilities
				.getConflictingChanges().size() == 0);
		assertTrue("Found incoming changes remaining.", CompareTestUtilities
				.getIncomingChanges().size() == 0);
		
		CompareTestUtilities.flushMergeEditor();

		m_sys = getSystemModel(projectName);		
		Interface_c iface = Interface_c.getOneC_IOnR8001(PackageableElement_c
				.getManyPE_PEsOnR8000(Package_c.getManyEP_PKGsOnR1405(m_sys)),
				new ClassQueryInterface_c() {

					@Override
					public boolean evaluate(Object candidate) {
						return ((Interface_c) candidate).getName().equals(
								"Pkg1");
					}
				});
		
		String[] orderedElements = new String[] { "m_op1", "m_op2", "sameName",
				"s_op1", "s_op2", "sameName", "m_sig1", "m_sig2", "sameName",
				"s_sig1", "s_sig2", "sameName" };
		
		verifyOrder(orderedElements, iface);
		
		// There should be no error log entries (shutdown will verify this)
	}

	public void testMergeClassSortedElements() throws Exception {
		String projectName = "GPS Watch";
		// import git repository from models repo
		GitUtil.resetRepository("sandbox", "slave2");
		// import test project
		GitUtil.loadProject(projectName, "sandbox", "slave2");
		// merge the test branch
		GitUtil.mergeBranch("master2", "sandbox", "slave2");
		// start the merge tool
		GitUtil.startMergeTool(projectName);

		// Merge Library::Location::GPS  (4 incoming changes in master, 4 outgoing changes from slave)
		CompareTestUtilities.copyAllNonConflictingChangesFromRightToLeft();
		// validate
		assertTrue("Found conflicting changes remaining.", CompareTestUtilities
				.getConflictingChanges().size() == 0);
		assertTrue("Found incoming changes remaining.", CompareTestUtilities
				.getIncomingChanges().size() == 0);
		
		CompareTestUtilities.flushMergeEditor();

		m_sys = getSystemModel(projectName);		
		ModelClass_c clazz = ModelClass_c.getOneO_OBJOnR8001(PackageableElement_c
				.getManyPE_PEsOnR8000(Package_c.getManyEP_PKGsOnR1405(m_sys)),
				new ClassQueryInterface_c() {

					@Override
					public boolean evaluate(Object candidate) {
						return ((ModelClass_c) candidate).getName().equals(
								"GPS");
					}
				});
		
		String[] orderedElements = new String[] { "currentLocation", "timer",
				"m_a1", "m_a2", "s_a1", "a_a2", "m_o1", "m_o2", "s_o1", "s_o2",
				"Class State Machine" };
		verifyOrder(orderedElements, clazz);

		
		// There should be no error log entries (shutdown will verify this)
	}

	public void testMergeClassSortedElementsWithMiddleAddition() throws Exception {
		// Merge Library::Tracking::WorkoutTimer (1 incoming change in master, 1 outgoing change from slave)
		String projectName = "GPS Watch";
		// import git repository from models repo
		GitUtil.resetRepository("sandbox", "slave3");
		// import test project
		GitUtil.loadProject(projectName, "sandbox", "slave3");
		// merge the test branch
		GitUtil.mergeBranch("master3", "sandbox", "slave3");
		// start the merge tool
		GitUtil.startMergeTool(projectName);

		CompareTestUtilities.copyAllNonConflictingChangesFromRightToLeft();
		// validate
		assertTrue("Found conflicting changes remaining.", CompareTestUtilities
				.getConflictingChanges().size() == 0);
		assertTrue("Found incoming changes remaining.", CompareTestUtilities
				.getIncomingChanges().size() == 0);
		
		CompareTestUtilities.flushMergeEditor();

		m_sys = getSystemModel(projectName);		
		ModelClass_c clazz = ModelClass_c.getOneO_OBJOnR8001(PackageableElement_c
				.getManyPE_PEsOnR8000(Package_c.getManyEP_PKGsOnR1405(m_sys)),
				new ClassQueryInterface_c() {

					@Override
					public boolean evaluate(Object candidate) {
						return ((ModelClass_c) candidate).getName().equals(
								"WorkoutTimer");
					}
				});
		
		String[] orderedElements = new String[] { "current_state", "time",
				"timer", "activate", "m_middle", "s_middle", "deactivate",
				"Instance State Machine" };
		verifyOrder(orderedElements, clazz);
	}
	
	
	public void testMergeInterfaceSortedElementsOneAddition() throws Exception {
		// Merge test1::iface1 (1 incoming change in master, 1 outgoing change from slave)
		String projectName = "GPS Watch";
		// import git repository from models repo
		GitUtil.resetRepository("sandbox", "slave4");
		// import test project
		GitUtil.loadProject(projectName, "sandbox", "slave4");
		// merge the test branch
		GitUtil.mergeBranch("master4", "sandbox", "slave4");
		// start the merge tool
		GitUtil.startMergeTool(projectName);

		CompareTestUtilities.copyAllNonConflictingChangesFromRightToLeft();
		// validate
		assertTrue("Found conflicting changes remaining.", CompareTestUtilities
				.getConflictingChanges().size() == 0);
		assertTrue("Found incoming changes remaining.", CompareTestUtilities
				.getIncomingChanges().size() == 0);
		
		CompareTestUtilities.flushMergeEditor();

		m_sys = getSystemModel(projectName);		
		Interface_c iface = Interface_c.getOneC_IOnR8001(PackageableElement_c
				.getManyPE_PEsOnR8000(Package_c.getManyEP_PKGsOnR1405(m_sys)),
				new ClassQueryInterface_c() {

					@Override
					public boolean evaluate(Object candidate) {
						return ((Interface_c) candidate).getName().equals(
								"iface1");
					}
				});
		
		String[] orderedElements = new String[] { "m_op1", "s_op1" };
		verifyOrder(orderedElements, iface);
		
		// There should be no error log entries (shutdown will verify this)
	}
	
	public void testCompareWith() throws Exception {
		String projectName = "GPS Watch";
		// import git repository from models repo
		GitUtil.resetRepository("sandbox", "slave1");
		// import test project
		GitUtil.loadProject(projectName, "sandbox", "slave1");
		
		GitUtil.compareWithBranch("master1", "sandbox", "slave1");

		
		CompareTestUtilities.openElementInSyncronizeView("Pkg1.xtuml");

		// Here are the changes:
		// Additions from the slave branch:
		//		"s_op1", "s_op2", "sameName", "sameName", "s_sig1", "s_sig2" };
		// Additions from the master branch:
		//		"m_op1", "m_op2", "sameName", "m_sig1", "m_sig2", 
		
		
		
		List<TreeDifference> actualResult = CompareTestUtilities.getChangesFromLeft(Differencer.NO_CHANGE, false);
		assertEquals("The correct totlal number of changes is present on the left", 12, actualResult.size());
				
		actualResult = CompareTestUtilities.getChangesFromRight(Differencer.NO_CHANGE, false);
		assertEquals("The correct totlal number of changes is present on the right", 12, actualResult.size());

		actualResult = CompareTestUtilities.getChangesFromLeft(Differencer.ADDITION, false);
		assertEquals("The number of expected additions is present on the left (additions from the slave branch)", 6, actualResult.size());
		
		actualResult = CompareTestUtilities.getChangesFromLeft(Differencer.DELETION, false);
		assertEquals("The number of expected additions is present on the left (additions from the master branch)", 6, actualResult.size());

		actualResult = CompareTestUtilities.getChangesFromRight(Differencer.ADDITION, false);
		assertEquals("The number of expected additions is present on the right (additions from the master branch)", 6, actualResult.size());
		
		actualResult = CompareTestUtilities.getChangesFromRight(Differencer.DELETION, false);
		assertEquals("The number of expected additions is present on the right (additions from the slave branch)", 6, actualResult.size());
		
		actualResult = CompareTestUtilities.getConflictingChanges();
		assertEquals("There are 0 conflicting changes", 0, actualResult.size());

		// There should be no error log entries (shutdown will verify this)
	}
	
	public void testFormalizedAssociationMerge() throws CoreException {
		// load the MO project from getting started
		loadProject("MicrowaveOven");
		BaseTest.dispatchEvents(0);
		m_sys = getSystemModel("MicrowaveOven");
		assertNotNull(m_sys);
		Package_c moPkg = Package_c.getOneEP_PKGOnR1405(m_sys,
				new ClassQueryInterface_c() {

					@Override
					public boolean evaluate(Object candidate) {
						return ((Package_c) candidate).getName().equals(
								"Microwave Oven");
					}
				});
		assertNotNull(moPkg);
		Association_c association = Association_c.getOneR_RELOnR8001(
				PackageableElement_c.getManyPE_PEsOnR8000(moPkg),
				new ClassQueryInterface_c() {

					@Override
					public boolean evaluate(Object candidate) {
						return ((Association_c) candidate).getNumb() == 4;
					}
				});
		assertNotNull(association);
		ModelClass_c oven = ModelClass_c.getOneO_OBJOnR8001(PackageableElement_c.getManyPE_PEsOnR8000(moPkg), new ClassQueryInterface_c() {
			
			@Override
			public boolean evaluate(Object candidate) {
				return ((ModelClass_c) candidate).getName().equals("Oven");
			}
		});
		ModelClass_c door = ModelClass_c.getOneO_OBJOnR8001(PackageableElement_c.getManyPE_PEsOnR8000(moPkg), new ClassQueryInterface_c() {
			
			@Override
			public boolean evaluate(Object candidate) {
				return ((ModelClass_c) candidate).getName().equals("Door");
			}
		});
		assertNotNull(oven);
		// delete R4
		Selection.getInstance().clear();
		Selection.getInstance().addToSelection(association);
		DeleteAction delete = new DeleteAction(null);
		delete.run();
		BaseTest.dispatchEvents(0);
		// merge both the MO package and the Oven class
		CompareTestUtilities.openCompareEditor(moPkg.getFile());
		BaseTest.dispatchEvents(0);
		CompareTestUtilities.copyAllNonConflictingChangesFromRightToLeft();
		CompareTestUtilities.closeMergeEditor(true);
		BaseTest.dispatchEvents(0);
		CompareTestUtilities.openCompareEditor(oven.getFile());
		BaseTest.dispatchEvents(0);
		CompareTestUtilities.copyAllNonConflictingChangesFromRightToLeft();
		CompareTestUtilities.closeMergeEditor(true);
		BaseTest.dispatchEvents(0);
		// assert that there are no O_REF instances that are pointing
		// to null O_RTIDA instances
		AttributeReferenceInClass_c[] orefs = AttributeReferenceInClass_c
				.getManyO_REFsOnR108(ReferentialAttribute_c
						.getManyO_RATTRsOnR106(Attribute_c
								.getManyO_ATTRsOnR102(oven)));
		for(AttributeReferenceInClass_c oref : orefs) {
			ReferredToIdentifierAttribute_c rtida = ReferredToIdentifierAttribute_c
					.getOneO_RTIDAOnR111(oref);
			assertNotNull("Found corruption in the class merge.", rtida);
		}
		// assert that there are no O_RTIDA instances that are pointing
		// to null O_OBJ instances
		ReferredToIdentifierAttribute_c[] rtidas = ReferredToIdentifierAttribute_c
				.getManyO_RTIDAsOnR110(ClassIdentifierAttribute_c
						.getManyO_OIDAsOnR105(Attribute_c
								.getManyO_ATTRsOnR102(door)));
		for(ReferredToIdentifierAttribute_c rtida : rtidas) {
			ModelClass_c clazz = ModelClass_c
					.getOneO_OBJOnR104(ClassIdentifier_c
							.getManyO_IDsOnR109(ReferredToClassInAssoc_c
									.getManyR_RTOsOnR110(rtida)));
			assertNotNull("Found corruption in the package merge.", clazz);
		}
	}
	
	public void testClassMergeWhenParticipatingInFormalizedAssociation() throws CoreException {
		loadProject("MicrowaveOven");
		BaseTest.dispatchEvents(0);
		m_sys = getSystemModel("MicrowaveOven");
		assertNotNull(m_sys);
		Package_c moPkg = Package_c.getOneEP_PKGOnR1405(m_sys,
				new ClassQueryInterface_c() {

					@Override
					public boolean evaluate(Object candidate) {
						return ((Package_c) candidate).getName().equals(
								"Microwave Oven");
					}
				});
		assertNotNull(moPkg);
		ModelClass_c oven = ModelClass_c.getOneO_OBJOnR8001(PackageableElement_c.getManyPE_PEsOnR8000(moPkg), new ClassQueryInterface_c() {
			
			@Override
			public boolean evaluate(Object candidate) {
				return ((ModelClass_c) candidate).getName().equals("Oven");
			}
		});
		// add an attribute to a class that is participating in a
		// formalized association
		CorePlugin
		.getDefault()
		.getPreferenceStore()
		.setValue(
				BridgePointPreferencesStore.USE_DEFAULT_NAME_FOR_CREATION,
				true);
		Selection.getInstance().clear(); Selection.getInstance().addToSelection(oven);
		NewAttributeOnO_OBJAction action = new NewAttributeOnO_OBJAction();
		action.run(null);
		BaseTest.dispatchEvents(0);
		// Merge the removal of the new attribute
		CompareTestUtilities.openCompareEditor(oven.getFile());
		BaseTest.dispatchEvents(0);
		CompareTestUtilities.copyAllNonConflictingChangesFromRightToLeft();
		CompareTestUtilities.closeMergeEditor(true);
		// assert that there are no O_REF instances that are pointing
		// to null O_RTIDA instances
		AttributeReferenceInClass_c[] orefs = AttributeReferenceInClass_c
				.getManyO_REFsOnR108(ReferentialAttribute_c
						.getManyO_RATTRsOnR106(Attribute_c
								.getManyO_ATTRsOnR102(oven)));
		for(AttributeReferenceInClass_c oref : orefs) {
			ReferredToIdentifierAttribute_c rtida = ReferredToIdentifierAttribute_c
					.getOneO_RTIDAOnR111(oref);
			assertNotNull("Found corruption in the class merge.", rtida);
		}
	}
	
	static void verifyOrder(String[] orderedElements, Object parent) {
		ModelContentProvider provider = new ModelContentProvider();
		ModelLabelProvider labelProvider = new ModelLabelProvider();
		Object[] children = provider.getChildren(parent);
		for (int i = 0; i < children.length; i++) {
			assertTrue(
					"Found unexpected element in the children list after merge.",
					labelProvider.getText(children[i]).equals(
							orderedElements[i]));
		}
	}


}
