package practice.chat.controller;

import org.xml.sax.SAXException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static practice.chat.util.ServletUtil.*;

public class RequestQueue {
    private static Queue<AsyncContext> asyncContextsQueue = new ConcurrentLinkedQueue<AsyncContext>();

    public static void addAsyncContext(AsyncContext asyncContext) {
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                removeAsyncContext(asyncContext);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                removeAsyncContext(asyncContext);
            }

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                removeAsyncContext(asyncContext);
            }
        });

        asyncContextsQueue.add(asyncContext);
    }

    public static void removeAsyncContext(AsyncContext asyncContext) {
        asyncContextsQueue.remove(asyncContext);
    }

    public static void replyAllClients() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        for(AsyncContext asyncContext : asyncContextsQueue) {
            String answer = getServerResponse();
            try {
                PrintWriter writer = asyncContext.getResponse().getWriter();
                writer.print(answer);
                writer.flush();
                asyncContext.complete();
            } catch(Exception e) {
                System.out.println(e);
            } finally {
                removeAsyncContext(asyncContext);
            }
        }
    }
}
