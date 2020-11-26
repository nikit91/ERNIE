package dice.nikit_thesis;

public class Dummy {
	
	public static void main(String[] args) {
		String temp = "<http://dbpedia.org/resource/Jean_Berko_Gleason/abstract#offset_0_877>\r\n" + 
				"	a nif:String, nif:OffsetBasedString, nif:Context ;\r\n" + 
				"	nif:isString \"\"\"Jean Berko Gleason (born 1931) is a professor emerita in the Department of Psychological and Brain Sciences (formerly the Department of Psychology) at Boston University, a psycholinguist who has made fundamental contributions to the understanding of language acquisition in children, aphasia, gender differences in language development, and parent-child interactions. Of her Wug Test, by which she demonstrated that even young children possess implicit knowledge of linguistic morphology, it has been said, \"Perhaps no innovation other than the invention of the tape recorder has had such an indelible effect on the field of child language research\", the \"wug\" (one of the imaginary creatures Gleason drew in creating the Wug Test) being \"so basic to what [psycholinguists] know and do that increasingly it appears in the popular literature without attribution to its origins.\"\"\"\"^^xsd:string;\r\n" + 
				"	nif:beginIndex \"0\"^^xsd:int;\r\n" + 
				"	nif:endIndex \"877\"^^xsd:int;\r\n" + 
				"	nif:sourceUrl <http://en.wikipedia.org/wiki/Jean_Berko_Gleason> .";
		
		String temp2 = "<http://dbpedia.org/resource/Baltimore_County%2C_Maryland/abstract#offset_811_824>\r\n" + 
				"	a nif:String, nif:OffsetBasedString ;\r\n" + 
				"	nif:referenceContext <http://dbpedia.org/resource/Baltimore_County%2C_Maryland/abstract#offset_0_5249> ;\r\n" + 
				"	nif:anchorOf \"\"\"\\\"megalopolis\\\"\"\"\"^^xsd:string ;\r\n" + 
				"	nif:beginIndex \"811\"^^xsd:int ;\r\n" + 
				"	nif:endIndex \"824\"^^xsd:int ;\r\n" + 
				"	prov:wasAttributedTo <http://wikipedia.org> ;\r\n" + 
				"	a nif:Word ;\r\n" + 
				"	itsrdf:taIdentRef <http://dbpedia.org/resource/Northeast_megalopolis> .";
		System.out.println(temp);
	}

}
