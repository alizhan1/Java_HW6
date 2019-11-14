package space.harbour.java.hw6;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebCrawler {

    private static ConcurrentLinkedQueue<URL> toVisit = new ConcurrentLinkedQueue<>();
    private static CopyOnWriteArraySet<URL> alreadyVisited = new CopyOnWriteArraySet<>();

    public static void extractUrls(String text) throws MalformedURLException {
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            URL url = new URL(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
            if (!alreadyVisited.contains(url) && url.toString().indexOf("facebook") == -1 && url.toString().indexOf("linkedin") == -1 && url.toString().indexOf("twitter") == -1 && url.toString().indexOf("google") == -1) {
                toVisit.add(url);
            }
        }
    }


    public static void getContentOfWebPage(URL url) throws MalformedURLException {
        final StringBuilder content = new StringBuilder();

        try ( InputStream is = url.openConnection().getInputStream();
              InputStreamReader in = new InputStreamReader(is, "UTF-8");
              BufferedReader br = new BufferedReader(in)) {
            String inputLine;
            while ((inputLine = br.readLine()) != null)
                content.append(inputLine);
        }
        catch (IOException e) {
            System.out.println("Failed to retrieve content of " + url.toString());
            e.printStackTrace();
        }
        alreadyVisited.add(url);
        extractUrls(content.toString());
    }

    public static void main(String[] args) throws MalformedURLException {
        URL u = new URL("http://www.zmiaikou.com/");
        toVisit.add(u);
        ExecutorService pool = Executors.newFixedThreadPool(5);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    getContentOfWebPage(toVisit.poll());
                }
                catch (MalformedURLException e) {
                    System.out.println("Shit, man you cought an exception!!");
                }
            }
        };

        pool.execute(task);
    }
}
