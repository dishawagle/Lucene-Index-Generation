package search;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;;


public class generateIndex {

public static String parseDoc(String f1,String f2,String DocText){
	int fromIndex = 0,a,b;
	String finalString = new String();
	while (DocText.indexOf(f1, fromIndex) >= 0) {
		a = DocText.indexOf(f1, fromIndex);
		b = DocText.indexOf(f2, fromIndex);
		a = a + f1.length();
		String t = DocText.substring(a, b);
		finalString += " " + t;
		fromIndex = b + f2.length(); 
	}
	return finalString;
}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File FolderDir = new File("C:\\Users\\Disha\\Downloads\\corpus");
		Directory IndexDir = FSDirectory.open(Paths.get("C:\\Users\\Disha\\Documents\\CIndex"));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(IndexDir, iwc);
		//iterating through files in corpus
		for(File i:FolderDir.listFiles()){
			BufferedReader br = new BufferedReader(new FileReader(i));
			StringBuffer str = new StringBuffer();
			String currentLine; 
			while ((currentLine = br.readLine()) != null) {
				str.append(currentLine);
				str.append('\n');
			}
		String s=str.toString();
		int fromIndex=0;
		int a,b;
		String comp="<DOC>";
		String comp2="</DOC>";
		while (s.indexOf(comp, fromIndex) >= 0) {
			a = s.indexOf(comp, fromIndex);
			b = s.indexOf(comp2, a + 5);
			String DocText = s.substring(a + 5, b);
			String[] fields={"HEAD","BYLINE","DATELINE","TEXT"};
			Document luceneDoc = new Document();
			luceneDoc.add(new Field(comp,parseDoc(comp,comp2,DocText),StringField.TYPE_STORED));
			for(String j:fields){
				luceneDoc.add(new Field(j,parseDoc("<"+j+">","</"+j+">",DocText),TextField.TYPE_STORED));
			}
			writer.addDocument(luceneDoc);
			fromIndex = b + 6;
		}
		
		}
		writer.forceMerge(1);
		writer.commit();
		writer.close();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(("C:\\Users\\Disha\\Documents\\CIndex"))));
		//Task 1: Number of documents
		System.out.println("Total number of documents in the corpus:" + reader.maxDoc());
		//Useful index statistics
		System.out.println("Number of documents containing the term new for	field TEXT:"+ reader.docFreq(new Term("TEXT", "new")));
		System.out.println("Number of occurrences of \"new\" in the field\"TEXT\": "+ reader.totalTermFreq(new Term("TEXT", "new")));
		Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		System.out.println("Size of the vocabulary for this field: " + vocabulary.size());
		System.out.println("Number of documents that have at least one term for this field: " + vocabulary.getDocCount());
		System.out.println("Number of tokens for this field: " + vocabulary.getSumTotalTermFreq());
		System.out.println("Number of postings for this field: " + vocabulary.getSumDocFreq());
		/*TermsEnum iterator = vocabulary.iterator();
		BytesRef byteRef = null;
		System.out.println("\n*******Vocabulary-Start**********");
		while((byteRef = iterator.next()) != null) {
		String term = byteRef.utf8ToString();
		System.out.print(term+"\t");
		}*/
		reader.close();
	}
		}

		
		



