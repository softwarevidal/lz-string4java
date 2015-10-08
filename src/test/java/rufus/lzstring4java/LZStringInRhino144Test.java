/*
* LZString4Java By Rufus Huang 
* https://github.com/rufushuang/lz-string4java
* MIT License
* 
* Port from original JavaScript version by pieroxy 
* https://github.com/pieroxy/lz-string
*/

package rufus.lzstring4java;


import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class LZStringInRhino144Test {

	private static final String LZ_STRING_JS_FILE_PATH = "lz-string-1.4.4.js";

	private Function FUNC_compressToEncodedURIComponent;
	private Function FUNC_decompressFromEncodedURIComponent;

	private String getJavaScript(){
		try(InputStream resourceStream = LZStringInRhino144Test.class.getResourceAsStream(LZ_STRING_JS_FILE_PATH)) {
			return IOUtils.toString(resourceStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Context context;
	private Scriptable scope;
	private NativeObject OBJ_LZString;
	private Function FUNC_compressToBase64;
	private Function FUNC_decompressFromBase64;
	private Function FUNC_compressToUTF16;
	private Function FUNC_decompressFromUTF16;
	private Function FUNC_compress;
	private Function FUNC_decompress;

	@Before
	public void setup() {
		// should change above JDK6, or just use Nashorn instead of Rhino?
		context = Context.enter();
		scope = context.initStandardObjects();
		context.evaluateString(scope, getJavaScript(), "script", 1, null);
		OBJ_LZString = (NativeObject) scope.get("LZString", scope);
		FUNC_compressToEncodedURIComponent = (Function) OBJ_LZString.get("compressToEncodedURIComponent", scope);
		FUNC_decompressFromEncodedURIComponent = (Function) OBJ_LZString.get("decompressFromEncodedURIComponent", scope);
		FUNC_compressToBase64 = (Function) OBJ_LZString.get("compressToBase64", scope);
		FUNC_decompressFromBase64 = (Function) OBJ_LZString.get("decompressFromBase64", scope);
		FUNC_compressToUTF16 = (Function) OBJ_LZString.get("compressToUTF16", scope);
		FUNC_decompressFromUTF16 = (Function) OBJ_LZString.get("decompressFromUTF16", scope);
		FUNC_compress = (Function) OBJ_LZString.get("compress", scope);
		FUNC_decompress = (Function) OBJ_LZString.get("decompress", scope);
	}

	public String compressToBase64(String input) {
		return (String) FUNC_compressToBase64.call(context, scope, scope, new Object[] { input });
	}

	public String decompressFromBase64(String input) {
		return (String) FUNC_decompressFromBase64.call(context, scope, scope, new Object[] { input });
	}

	public String compressToUTF16(String input) {
		return (String) FUNC_compressToUTF16.call(context, scope, scope, new Object[] { input });
	}

	public String decompressFromUTF16(String input) {
		return (String) FUNC_decompressFromUTF16.call(context, scope, scope, new Object[] { input });
	}

	public String compress(String input) {
		return (String) FUNC_compress.call(context, scope, scope, new Object[] { input });
	}

	public String decompress(String input) {
		return (String) FUNC_decompress.call(context, scope, scope, new Object[] { input });
	}

	public String compressToEncodedURIComponent(String input) {
		return (String) FUNC_compressToEncodedURIComponent.call(context, scope, scope, new Object[] { input });
	}

	public String decompressFromEncodedURIComponent(String input) {
		return (String) FUNC_decompressFromEncodedURIComponent.call(context, scope, scope, new Object[] { input });
	}

	@Test
	public void testSanityJSImpl() throws IOException {
		String input = "hello1hello2hello3hello4hello5hello6hello7hello8hello9helloAhelloBhelloChelloDhelloEhelloF";

		assertEquals(input, decompress(compress(input)));
		assertEquals(input, decompressFromBase64(compressToBase64(input)));
		assertEquals(input, decompressFromUTF16(compressToUTF16(input)));
	}

	@Test
	public void testJSCompressJavaDecompress() throws IOException {
		for(String fuzz: fuzzInput(200)) {
			assertEquals(fuzz, LZString.decompress(compress(fuzz)));
			assertEquals(fuzz, LZString.decompressFromBase64(compressToBase64(fuzz)));
			assertEquals(fuzz, LZString.decompressFromUTF16(compressToUTF16(fuzz)));
		}
	}

	@Test
	public void testJavaCompressJavaDecompress() throws IOException {
		for(String fuzz: fuzzInput(200)) {
			assertEquals(fuzz, LZString.decompress(LZString.compress(fuzz)));
			assertEquals(fuzz, LZString.decompressFromBase64(LZString.compressToBase64(fuzz)));
			assertEquals(fuzz, LZString.decompressFromUTF16(LZString.compressToUTF16(fuzz)));
		}
	}

	@Test
	public void testOnlyAlphaNum() throws IOException {
		for(String fuzz: fuzzAlphaNum(200)) {
			assertEquals(fuzz, LZString.decompress(compress(fuzz)));
		}
	}

	@Test
	public void testJavaCompressJSDecompress() throws IOException {
		for(String fuzz: fuzzInput(200)) {
			assertEquals(fuzz, decompress(LZString.compress(fuzz)));
			assertEquals(fuzz, decompressFromUTF16(LZString.compressToUTF16(fuzz)));
			assertEquals(fuzz, decompressFromBase64(LZString.compressToBase64(fuzz)));
		}
	}



	@Test
	public void testSanityJSImplEncodedURIComponent() throws IOException {
		String input = "hello1hello2hello3hello4hello5hello6hello7hello8hello9helloAhelloBhelloChelloDhelloEhelloF";
		assertEquals(input, decompressFromEncodedURIComponent(compressToEncodedURIComponent(input)));
	}

	@Test
	public void testJSCompressJavaDecompressEncodedURIComponent() throws IOException {
		for(String fuzz: fuzzInput(200)) {
			assertEquals(fuzz, LZString.decompressFromEncodedURIComponent(compressToEncodedURIComponent(fuzz)));
		}
	}

	@Test
	public void testJavaCompressJSDecompressEncodedURIComponent() throws IOException {
		for(String fuzz: fuzzInput(200)) {
			assertEquals(fuzz, decompressFromEncodedURIComponent(LZString.compressToEncodedURIComponent(fuzz)));
		}
	}

	public List<String> fuzzInput(int len) {
		List<String> fuzz = new ArrayList<>();
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < len; i++){
			sb.append((char)r.nextInt(1024));
			fuzz.add(sb.toString());
		}
		return fuzz;
	}

	public List<String> fuzzAlphaNum(int len) {
		List<String> fuzz = new ArrayList<>();
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < len; i++){
			sb.append((char)(r.nextInt(26) + 65));
			fuzz.add(sb.toString());
		}
		return fuzz;
	}


}
