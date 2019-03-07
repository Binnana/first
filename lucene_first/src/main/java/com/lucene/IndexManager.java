package com.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class IndexManager {

    /*1.创建索引*/
    @Test
    public void testWriter() throws IOException {

        FSDirectory directory = FSDirectory.open(new File("D:\\JavaEE61Code\\index_repo"));

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,analyzer);

        IndexWriter indexWriter = new IndexWriter(directory,config);

        indexWriter.deleteAll();

        File filePaths = new File("D:\\百度网盘\\Java学习资料\\07_项目一\\day01_Lucene\\资料\\上课用的查询资料searchsource");
        File[] files = filePaths.listFiles();
        for (File file : files) {
            Document document = new Document();
            String fileName = file.getName();
            document.add(new TextField("filename",fileName, Field.Store.YES));

            String fileContent = FileUtils.readFileToString(file, "utf-8");
            document.add(new TextField("filecontent",fileContent, Field.Store.YES));

            String filePath = file.getPath();
            document.add(new TextField("filepath",filePath, Field.Store.YES));

            Long fileSize = FileUtils.sizeOf(file);
            document.add(new LongField("filesize",fileSize, Field.Store.YES));

             indexWriter.addDocument(document);
        }

        indexWriter.close();
    }

    /*从索引中查询*/
    @Test
    public void indexReader() throws IOException {
        Directory directory = FSDirectory.open(new File("D:\\JavaEE61Code\\index_repo"));

        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("filename","apache"));
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);

            System.out.println("标题："+document.get("filename"));
//            System.out.println("内容："+document.get("filecontent"));
            System.out.println("路径："+document.get("filepath"));
            System.out.println("大小："+document.get("filesize"));
        }

        indexReader.close();
    }
}
