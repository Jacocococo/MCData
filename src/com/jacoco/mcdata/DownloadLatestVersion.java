package com.jacoco.mcdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class DownloadLatestVersion {

	public static void main(String[] args) throws Exception {
		URL manifestUrl = new URL("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");
		JSONObject jo1 = (JSONObject) JSONValue.parse(new InputStreamReader(manifestUrl.openStream()));
		JSONObject version = (JSONObject) ((JSONArray) jo1.get("versions")).get(0);
		String id = version.get("id").toString();
		URL url = new URL(version.get("url").toString());;
		File versionsDir = new File(Utils.getMinecraftDir(), "versions");
		File versionDir = new File(versionsDir, id);
		versionDir.mkdirs();

		JSONObject jo2 = (JSONObject) JSONValue.parse(new InputStreamReader(url.openStream()));
		PrintWriter writer = new PrintWriter(new File(versionDir, id + ".json"));
		writer.write(jo2.toString());
		writer.close();
		
		URL jarUrl = new URL(((JSONObject) ((JSONObject) jo2.get("downloads")).get("client")).get("url").toString());
		ReadableByteChannel channel = Channels.newChannel(jarUrl.openStream());
		FileOutputStream outStream = new FileOutputStream(new File(versionDir, id + ".jar"));
		outStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
		outStream.close();
		channel.close();
	}
}
