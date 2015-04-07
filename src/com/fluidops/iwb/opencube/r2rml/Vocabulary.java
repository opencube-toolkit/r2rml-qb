package com.fluidops.iwb.opencube.r2rml;

import org.openrdf.model.URI;

import com.fluidops.iwb.util.RDFUtil;

public class Vocabulary {

	private Vocabulary() {
		// TODO Auto-generated constructor stub
	}
	
	public static class RRQB {
				
		public static final String NAMESPACE = "http://www.opencube-project.eu/ontologies/r2rml-cube#";
		
		public static final URI CONTAINS_CUBE_MAP = RDFUtil.fullUri(NAMESPACE + "containsCubeMap");
		
	}

}
