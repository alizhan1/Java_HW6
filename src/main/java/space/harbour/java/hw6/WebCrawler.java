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

    private List<URL> neverVisit = new ArrayList<>();
    private ConcurrentLinkedQueue<URL> toVisit = new ConcurrentLinkedQueue<>();
    private CopyOnWriteArraySet<URL> alreadyVisited = new CopyOnWriteArraySet<>();

    public void extractUrls(String text) throws MalformedURLException {
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            URL url = new URL(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
            if (!alreadyVisited.contains(url)) {
                toVisit.add(url);
            }
        }
    }


    public static String getContentOfWebPage(URL url) {
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

        return content.toString();
    }

    public static void main(String[] args) throws MalformedURLException {
        URL u = new URL("https://stackoverflow.com/questions/5713558/detect-and-extract-url-from-a-string");
        String c = getContentOfWebPage(u);
        System.out.println(c);
//        ExecutorService pool = Executors.newFixedThreadPool(10);
//        Callable<Integer> task = new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//
//            }

//        };
//
//
//        Future<Integer> future1 = pool.submit(task);
//        Future<Integer> future2 = pool.submit(task);
//
//        try {
//            System.out.println(future1.get());
//            System.out.println(future2.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }
}
