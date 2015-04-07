package com.fluidops.iwb.opencube.r2rml;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.ListBindingSet;
import org.openrdf.query.impl.MutableTupleQueryResult;

import com.fluidops.iwb.annotation.CallableFromWidget;
import com.fluidops.iwb.api.ReadDataManager;
import com.fluidops.iwb.api.query.JavaQueryContext;
import com.fluidops.iwb.model.Vocabulary.RR;
import com.fluidops.iwb.r2rml.api.LogicalTable;
import com.fluidops.iwb.r2rml.api.impl.LogicalTableImpl;
import com.fluidops.iwb.r2rml.api.impl.TriplesMapCollectionImpl;
import com.fluidops.iwb.util.RDFUtil;
import com.fluidops.iwb.util.sql.SqlMetaUtil;
import com.fluidops.util.Rand;
import com.google.common.collect.Lists;

public class R2RMLCubeEditorUtils {

	private R2RMLCubeEditorUtils() {
	
	}

	@CallableFromWidget
	public static void addObjectMap(URI cubePredicateObjectMapURI) {
		URI instanceURI = RDFUtil.fullUri("http://www.fluidops.com/resource/ObjectMap_" + Rand.getFluidUUID());
		
		List<Statement> toAdd = Lists.newLinkedList();
		toAdd.add(RDFUtil.stmt(instanceURI, RDF.TYPE, RR.OBJECT_MAP_TYPE));
		toAdd.add(RDFUtil.stmt(cubePredicateObjectMapURI, RR.OBJECT_MAP, instanceURI));
		
		RDFUtil.writeStatements(toAdd);
	}
	
	@CallableFromWidget
	public static void addObjectMapWithType(URI cubePredicateObjectMapURI, String type) {
		URI instanceURI = RDFUtil.fullUri("http://www.fluidops.com/resource/ObjectMap_" + Rand.getFluidUUID());
		
		List<Statement> toAdd = Lists.newLinkedList();
		toAdd.add(RDFUtil.stmt(instanceURI, RDF.TYPE, RR.OBJECT_MAP_TYPE));
		toAdd.add(RDFUtil.stmt(cubePredicateObjectMapURI, RR.OBJECT_MAP, instanceURI));
		
		toAdd.add(RDFUtil.stmt(instanceURI, RDFUtil.uri("rrqb:objectMapType"), RDFUtil.literal(type)));
		RDFUtil.writeStatements(toAdd);
	}
	
	
	@CallableFromWidget
	public static void deleteCubePredicateObjectMap(String sMapObjectUri) {
		
		List<Statement> toDelete = Lists.newLinkedList();
		
		URI mapObjectURI = RDFUtil.uri(sMapObjectUri);
		
		Iterable<Statement> stmts = RDFUtil.readStatements(null, null, mapObjectURI);
		for(Statement stmt : stmts)
			toDelete.add(stmt);
		
		stmts = RDFUtil.readStatements(mapObjectURI, null, null);
		
		for(Statement stmt : stmts) {
			if(stmt.getPredicate().equals(RR.OBJECT_MAP)) {
				URI objMap = (URI)stmt.getObject();
				
				Iterable<Statement> stmts2 = RDFUtil.readStatements(objMap, null, null);
				for(Statement stmt2 : stmts2) 
					toDelete.add(stmt2);
				
			}
			
			toDelete.add(stmt);
		}
		
		RDFUtil.removeStatements(toDelete);
		
	}
	
	
	@CallableFromWidget
	public static TupleQueryResult getLogicalTableColumnNames(JavaQueryContext ctx) throws Exception {
		
		try {
			URI cubeMapURI = (URI)ctx.getContextValue();
			
			ReadDataManager dm = ctx.getDataManager();
			
			URI triplesMapCollectionURI = (URI)dm.getInverseProp(cubeMapURI, Vocabulary.RRQB.CONTAINS_CUBE_MAP);
			
			TriplesMapCollectionImpl triplesMapCollection = dm.getProp(triplesMapCollectionURI, TriplesMapCollectionImpl.class); 
			
			LogicalTable lt = dm.getProp(cubeMapURI, com.fluidops.iwb.model.Vocabulary.RR.LOGICAL_TABLE, LogicalTableImpl.class);
			
			List<String> colNames = SqlMetaUtil.getColumnNamesQuery(lt, triplesMapCollection.getConnectionInfo());
			
			List<String> bindingNames = Lists.newArrayList("columnName");
			
			List<BindingSet> bindingSets = Lists.newArrayListWithCapacity(colNames.size());
			
			for(String colName : colNames) {
				BindingSet bs = new ListBindingSet(bindingNames, Lists.newArrayList(RDFUtil.literal(colName)));
				bindingSets.add(bs);
			}
			
			TupleQueryResult res = new MutableTupleQueryResult(bindingNames, bindingSets);
			
			return res;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
}
