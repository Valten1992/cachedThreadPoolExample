import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class test {

	static String[] keywords = new String[] { "Sausages", "Apples", "Bacon",
			"Trilithium Converters", "Stock Dog images", "Mash", "Excalibur", "Wings", "Tangoes", "Tardis's", "Oranges", "Fruit", }; //.......

	public static void main(String[] args) throws Exception {

		//Define a cachedPool that uses the CustomThreadPoolExecutor
		ExecutorService cachedPool = new CustomThreadPoolExecutor(10, 50, 4000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		
		Callable<String> callable = null;
		Future<String> callableFuture = null;

		//Loop through the keywords and create a thread for each (depending on the amount f)
		for (int i = 0; i < keywords.length; i++) {

			callable = new callableRequest(keywords[i]);

			callableFuture = cachedPool.submit(callable);
		
			try {
				// get() waits for the task to finish and then gets the result
				String returnedValue = callableFuture.get();
				System.out.println(returnedValue);
			} catch (InterruptedException e) {
				// thrown if task was interrupted before completion
				e.printStackTrace();
			} catch (ExecutionException e) {
				// thrown if the task threw an exception while executing
				e.printStackTrace();
			}
		}
		
		cachedPool.shutdown(); // shutdown the pool
	}
}

//Callable class representing a request
class callableRequest implements Callable<String> {

	String key;

	public callableRequest(String key) {

		this.key = key;
	}

	@Override
	public String call() throws Exception {

		//Create HTTP client
		String url = "http://somecompany.com/api/categorise?";
		HttpClient client = HttpClientBuilder.create().build();
		//HttpGet post = new HttpGet(url); //Testing if requests are indeed executed in parallel
		HttpPost post = new HttpPost(url);

		//Add parameters to the POST command
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("apikey", "ABC123"));
		urlParameters.add(new BasicNameValuePair("keyword", key));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		//Catch the response
		HttpResponse response = client.execute(post);
		System.out.println("Response Code : "
				+ response.getStatusLine().getStatusCode());

		//Create a string of the result data
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		while ((rd.readLine()) != null) {
			result.append(rd.readLine() + "%n");
		}

		//I am unsure if the actual webpage would just return plaintext or not, I have included a regular expression to remove HTML but this should be
		//considered heavily as regex commands are costly to implement and may not scale well
		String finalResult = result.toString();//.replaceAll("\\<[^>]*>","");
		return finalResult;
	}

}
