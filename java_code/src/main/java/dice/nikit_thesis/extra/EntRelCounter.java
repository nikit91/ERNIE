package dice.nikit_thesis.extra;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class EntRelCounter {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		model.read(args[0]);
		
		Set<Property> relations = new HashSet<Property>();
		Set<Resource> entities = new HashSet<Resource>();
		
		StmtIterator stmtIter = model.listStatements();
		while(stmtIter.hasNext()) {
			Statement curStmt = stmtIter.next();
			RDFNode objectNode = curStmt.getObject();
			
			relations.add(curStmt.getPredicate());
			entities.add(curStmt.getSubject());
			if(objectNode.isURIResource()) {
				entities.add(objectNode.asResource());
			}
		}
		
		System.out.println("Unique entities: "+entities.size());
		System.out.println("Unique relations: "+relations.size());
	}

}
