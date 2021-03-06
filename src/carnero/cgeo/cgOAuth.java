package carnero.cgeo;

import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class cgOAuth {
	public static String signOAuth(String host, String path, String method, boolean https, HashMap<String, String> params, String token, String tokenSecret) {
		String paramsDone = "";
		if (method.equalsIgnoreCase("GET") == false && method.equalsIgnoreCase("POST") == false) {
			method = "POST";
		} else {
			method = method.toUpperCase();
		}

		if (token == null) token = "";
		if (tokenSecret == null) tokenSecret = "";

		long currentTime = new Date().getTime(); // miliseconds
		currentTime = currentTime / 1000; // seconds
		currentTime = (long)Math.floor(currentTime);

		params.put("oauth_consumer_key", cgSettings.keyConsumerPublic);
		params.put("oauth_nonce", cgBase.md5(Long.toString(System.currentTimeMillis())));
		params.put("oauth_signature_method", "HMAC-SHA1");
		params.put("oauth_timestamp", Long.toString(currentTime));
		params.put("oauth_token", token);
		params.put("oauth_version", "1.0");

		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);

		ArrayList<String> paramsEncoded = new ArrayList();
		for (int i = 0; i < keys.length; i++) {
			String value = params.get(keys[i].toString());

			paramsEncoded.add(keys[i] + "=" + cgBase.urlencode_rfc3986(value.toString()));
		}

		String keysPacked;
		String requestPacked;

		keysPacked = cgSettings.keyConsumerSecret + "&" + tokenSecret; // both even if empty some of them!
		if (https == true) requestPacked = method + "&" + cgBase.urlencode_rfc3986("https://" + host + path) + "&" + cgBase.urlencode_rfc3986(cgBase.implode("&", paramsEncoded.toArray()));
		else requestPacked = method + "&" + cgBase.urlencode_rfc3986("http://" + host + path) + "&" + cgBase.urlencode_rfc3986(cgBase.implode("&", paramsEncoded.toArray()));
		paramsEncoded.add("oauth_signature=" + cgBase.urlencode_rfc3986(cgBase.base64Encode(cgBase.hashHmac(requestPacked, keysPacked))));

		paramsDone = cgBase.implode("&", paramsEncoded.toArray());

		return paramsDone;
	}
}