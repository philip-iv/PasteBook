package me.philipiv.pastebook;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.plugin.java.JavaPlugin;

public class PasteBook extends JavaPlugin {
	private static enum BookVersion {
		ONE_ZERO("1.0");
		private static Map<String, BookVersion> versions = new HashMap<String, BookVersion>();
		static {
			for (BookVersion bv : EnumSet.allOf(BookVersion.class)) {
				versions.put(bv.version, bv);
			}
		}
		private String version;
		BookVersion(String version) {
			this.version = version;
		}
		
		public static BookVersion getBookVersion(String version) {
			return versions.get(version);
		}
	}
	private BookVersion version = BookVersion.ONE_ZERO;
	private String postUrl = "http://pastebin.com/api/api_post.php";
	private String getUrl = "http://pastebin.com/raw/";
	private String apiKey = "7c44715cb3c6feac8054c261328c58a8";
	private HttpClient client = HttpClients.createDefault();
	
	@Override
	public void onEnable() {
		getCommand("upload").setExecutor(new UploadCommand(this));
		getCommand("download").setExecutor(new DownloadCommand(this));
	}
	
	public String upload(BookMeta book) {
		HttpPost post = new HttpPost(postUrl);
		String text = "";
		text += version.version + "\n";
		text += book.getTitle() + "\n";
		text += book.getAuthor() + "\n";
		for (int i = 1; i <= book.getPageCount(); i++)
			text += book.getPage(i) + "\n\n";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_dev_key", apiKey));
		params.add(new BasicNameValuePair("api_option", "paste"));
		params.add(new BasicNameValuePair("api_paste_code", text));
		params.add(new BasicNameValuePair("api_paste_name", book.getTitle() + " by " + book.getAuthor()));
		post.setEntity(new UrlEncodedFormEntity(params, Charset.forName("UTF-8")));
		
		try {
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
		        return EntityUtils.toString(entity);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public BookMeta download(String id) throws IOException {
		HttpGet get = new HttpGet(getUrl + id);
		HttpResponse response;
		response = client.execute(get);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String book = EntityUtils.toString(entity);
			int split = book.indexOf("\n");
			String version = book.substring(0, split);
			BookVersion bv = BookVersion.getBookVersion(version);
			return parseBook(book.substring(split +1), bv);
		}
		return null;
	}
	
	private BookMeta parseBook(String book, BookVersion version) {
		BookMeta bm = (BookMeta) new ItemStack(Material.WRITTEN_BOOK).getItemMeta();
		switch (version) {
		case ONE_ZERO:
			int firstBreak = book.indexOf("\n");
			int secondBreak = book.indexOf("\n", firstBreak + 1);
			String title = book.substring(0, firstBreak);
			getLogger().info(title);
			bm.setTitle(title);
			String author = book.substring(firstBreak + 1, secondBreak);
			getLogger().info(author);
			bm.setAuthor(author);
			bm.setPages(book.substring(secondBreak).split("\n\n"));
			bm.setGeneration(Generation.COPY_OF_ORIGINAL);
		}
		return bm;
	}
}