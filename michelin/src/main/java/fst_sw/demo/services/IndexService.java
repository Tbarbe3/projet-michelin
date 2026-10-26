package fst_sw.demo.services;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IndexService {

    private       DirectoryReader directoryReader;
    private       IndexSearcher   indexSearcher;
    private final IndexWriter     indexWriter;
    private final QueryParser     commentQueryParser;

    public IndexService() throws IOException {
        String    indexDirectoryPath = "index";
        Path      path               = Paths.get(indexDirectoryPath);
        Directory index              = FSDirectory.open(path);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        this.commentQueryParser = new QueryParser("content", analyzer);

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(index, indexWriterConfig);

        if (DirectoryReader.indexExists(index)) {
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        } else {
            // si l'index n'existe pas on le creé
            this.indexWriter.commit();
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        }
    }

    public void indexComment(UUID serieId, UUID commentId, String comment) {
        try {
            Document document = new Document();
            document.add(new StringField("commentId", String.valueOf(commentId), Field.Store.YES));
            document.add(new StringField("serieId", String.valueOf(serieId), Field.Store.YES));
            document.add(new TextField("content", comment, Field.Store.YES));
            this.indexWriter.addDocument(document);
            this.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteComment(UUID commentId) {
        try {
            this.indexWriter.deleteDocuments(new Term("commentId", String.valueOf(commentId)));
            this.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSerieComments(UUID serieId) {
        try {
            this.indexWriter.deleteDocuments(new Term("serieId", String.valueOf(serieId)));
            this.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Retourne les ids des commentaires de la série qui contiennent le mot
    public List<UUID> searchComments(UUID serieId, String word) {
        try {
            Query query = new BooleanQuery.Builder()
                    .add(new TermQuery(new Term("serieId", String.valueOf(serieId))), BooleanClause.Occur.MUST)
                    .add(this.commentQueryParser.parse(QueryParser.escape(word)), BooleanClause.Occur.MUST)
                    .build();

            List<UUID> ids = new ArrayList<>();
            for (ScoreDoc hit : this.indexSearcher.search(query, 100).scoreDocs) {
                ids.add(UUID.fromString(this.indexSearcher.storedFields().document(hit.doc).get("commentId")));
            }
            return ids;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // Enregistre les modifications et recharge le searcher
    private void refresh() throws IOException {
        this.indexWriter.commit();

        DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(this.directoryReader);
        if (newDirectoryReader != null) {
            this.directoryReader.close();
            this.directoryReader = newDirectoryReader;
            this.indexSearcher = new IndexSearcher(this.directoryReader);
        }
    }
}
