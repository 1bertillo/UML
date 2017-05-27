package com.uniovi.generator;

import java.io.File;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.common.util.UML2Util;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

public class UMLDiagram {
	public boolean DEBUG = true;
	public Model model;
	private File outputDir;
	public UMLDiagram() {
		this.model = UMLFactory.eINSTANCE.createModel();
		this.model.setName("model");
		
		 out("Model '%s' created.", model.getQualifiedName());
		this.outputDir = new File("GeneratedModel");
	}
	
	public Model getModel(){
		return this.model;
	}

	protected void out(String format, Object... args) {
		if (DEBUG) {
			System.out.printf(format, args);
			if (!format.endsWith("%n")) {
				System.out.println();
			}
		}
	}

	protected void err(String format, Object... args) {
		System.err.printf(format, args);
		if (!format.endsWith("%n")) {
			System.err.println();
		}
	}
    protected org.eclipse.uml2.uml.Package createPackage(org.eclipse.uml2.uml.Package nestingPackage, String name) {
        org.eclipse.uml2.uml.Package package_ = nestingPackage.createNestedPackage(name);

        out("Package '%s' created.", package_.getQualifiedName());

        return package_;
    }
    public PrimitiveType createPrimitiveType(String name) {
        PrimitiveType primitiveType = this.model.createOwnedPrimitiveType(name);

        out("Primitive type '%s' created.", primitiveType.getQualifiedName());

        return primitiveType;
    }
    
	public org.eclipse.uml2.uml.Class createClass(String name) {
		org.eclipse.uml2.uml.Class class_ = this.model.createOwnedClass(name, false);

		out("Class '%s' created.", class_.getQualifiedName());

		return class_;
	}

	public Generalization createGeneralization(Classifier specificClassifier, Classifier generalClassifier) {
		Generalization generalization = specificClassifier.createGeneralization(generalClassifier);

		out("Generalization %s --|> %s created.", specificClassifier.getQualifiedName(),
				generalClassifier.getQualifiedName());

		return generalization;
	}

	public Association createAssociation(Type type1,
			boolean end1IsNavigable, AggregationKind end1Aggregation,
			String end1Name, int end1LowerBound, int end1UpperBound,
			Type type2, boolean end2IsNavigable,
			AggregationKind end2Aggregation, String end2Name,
			int end2LowerBound, int end2UpperBound) {

		Association association = type1.createAssociation(end1IsNavigable,
			end1Aggregation, end1Name, end1LowerBound, end1UpperBound, type2,
			end2IsNavigable, end2Aggregation, end2Name, end2LowerBound,
			end2UpperBound);

		out("Association %s [%s..%s] %s-%s %s [%s..%s] created.", //
			UML2Util.isEmpty(end1Name)
				// compute a placeholder for the name
				? String.format("{%s}", type1.getQualifiedName()) //
				// user-specified name
				: String.format("'%s::%s'", type1.getQualifiedName(), end1Name), //
			end1LowerBound, // no special case for this
			(end1UpperBound == LiteralUnlimitedNatural.UNLIMITED)
				? "*" // special case for unlimited upper bound
				: end1UpperBound, // finite upper bound
			end2IsNavigable
				? "<" // indicate navigability
				: "-", // not navigable
			end1IsNavigable
				? ">" // indicate navigability
				: "-", // not navigable
			UML2Util.isEmpty(end2Name)
				// compute a placeholder for the name
				? String.format("{%s}", type2.getQualifiedName()) //
				// user-specified name
				: String.format("'%s::%s'", type2.getQualifiedName(), end2Name), //
			end2LowerBound, // no special case for this
			(end2UpperBound == LiteralUnlimitedNatural.UNLIMITED)
				? "*" // special case for unlimited upper bound
				: end2UpperBound);

		return association;
	}

	public Property createAttribute(org.eclipse.uml2.uml.Class class_, String name, Type type, int lowerBound,
			int upperBound) {
		Property attribute = class_.createOwnedAttribute(name, type, lowerBound, upperBound);

		out("Attribute '%s' : %s [%s..%s] created.", //
				attribute.getQualifiedName(), // attribute name
				type.getQualifiedName(), // type name
				lowerBound, // no special case for multiplicity lower bound
				(upperBound == LiteralUnlimitedNatural.UNLIMITED) ? "*" // special
																		// case
																		// for
																		// unlimited
																		// bound
						: upperBound);

		return attribute;
	}
	public Dependency createDependency(Type type1,Type supplier){
		return type1.createDependency(supplier);
	}

	protected void saveModel() {
		// Create a resource-set to contain the resource(s) that we are saving
		ResourceSet resourceSet = new ResourceSetImpl();

		// Initialize registrations of resource factories, library models,
		// profiles, Ecore metadata, and other dependencies required for
		// serializing and working with UML resources. This is only necessary in
		// applications that are not hosted in the Eclipse platform run-time, in
		// which case these registrations are discovered automatically from
		// Eclipse extension points.
		//UMLResourcesUtil.init(resourceSet);
		URI uri =  URI.createFileURI(System.getProperty("user.dir"));

		// Create the output resource and add our model package to it.
		Resource resource = resourceSet.createResource(uri);
		resource.getContents().add(this.model);

		// And save
		

		try {
			resource.save(null); // no save options needed
			out("Done.");
		} catch (IOException ioe) {
			err(ioe.getMessage());
		}
	}
}
