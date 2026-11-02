package fst_sw.michelin.services;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {
    private DirectoryReader directoryReader;
    private IndexSearcher indexSearcher;
    private final IndexWriter indexWriter;
    private final QueryParser commentQueryParser;

    public IndexService() throws IOException {
        String    indexDirectoryPath = "index";
        Path path = Paths.get(indexDirectoryPath);
        Directory index = FSDirectory.open(path);
        StandardAnalyzer analyzer = new StandardAnalyzer();
        this.commentQueryParser = new QueryParser("content", analyzer);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(index, indexWriterConfig);
        if (!DirectoryReader.indexExists(index)) {
            this.indexWriter.commit();
        }
        this.directoryReader = DirectoryReader.open(index);
        this.indexSearcher = new IndexSearcher(directoryReader);
    }

    public void indexEvaluation(int restaurantId, int evaluationId, String evaluation) {
        try {
            Document document = new Document();
            document.add(new StringField("evaluationId", String.valueOf(evaluationId), Field.Store.YES));
            document.add(new StringField("restaurantId", String.valueOf(restaurantId), Field.Store.YES));
            document.add(new TextField("content", evaluation, Field.Store.YES));
            this.indexWriter.addDocument(document);
            this.indexWriter.commit();
            DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(this.directoryReader);
            if (newDirectoryReader != null) {
                this.directoryReader.close();
                this.directoryReader = newDirectoryReader;
                this.indexSearcher = new IndexSearcher(this.directoryReader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEvaluation(int evaluationId) {
        try {
            this.indexWriter.deleteDocuments(new Term("evaluationId", String.valueOf(evaluationId)));
            this.indexWriter.commit();
            DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(this.directoryReader);
            if (newDirectoryReader != null) {
                this.directoryReader.close();
                this.directoryReader = newDirectoryReader;
                this.indexSearcher = new IndexSearcher(this.directoryReader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> searchEvaluations(String word) {
        try {
            Query query = this.commentQueryParser.parse(QueryParser.escape(word));
            TopDocs topDocs = this.indexSearcher.search(query, 100);
            StoredFields storedFields = this.indexSearcher.storedFields();
            List<Integer> commentIds = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = storedFields.document(scoreDoc.doc);
                commentIds.add(Integer.parseInt(document.get("commentId")));
            }
            return commentIds;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}