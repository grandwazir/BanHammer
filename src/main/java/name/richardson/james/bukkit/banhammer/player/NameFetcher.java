/*******************************************************************************
 Copyright (c) 2014 evilmidget38, James Richardson (my portions under the GPL).

 NameFetcher.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.player;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

import com.google.common.collect.ImmutableList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class NameFetcher implements Callable<Map<UUID, String>> {

	private static final Map<UUID, String> CACHE = Collections.synchronizedMap(new WeakHashMap<UUID, String>());
	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

	private final JSONParser jsonParser = new JSONParser();
	private final List<UUID> uuids;

	public NameFetcher(List<UUID> uuids) {
		this.uuids = ImmutableList.copyOf(uuids);
	}

	@Override
	public Map<UUID, String> call() throws Exception {
		Map<UUID, String> uuidStringMap = new HashMap<UUID, String>();
		for (UUID uuid: uuids) {
			HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL+uuid.toString().replace("-", "")).openConnection();
			JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
			String name = (String) response.get("name");
			if (name == null) {
				continue;
			}
			String cause = (String) response.get("cause");
			String errorMessage = (String) response.get("errorMessage");
			if (cause != null && cause.length() > 0) {
				throw new IllegalStateException(errorMessage);
			}
			uuidStringMap.put(uuid, name);
		}
		CACHE.putAll(uuidStringMap);
		return uuidStringMap;
	}

	public static String getNameOf(UUID uuid) {
		if (!CACHE.containsKey(uuid)) {
			final NameFetcher nameFetcher = new NameFetcher(Arrays.asList(uuid));
			try {
				CACHE.putAll(nameFetcher.call());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return CACHE.get(uuid);
	}

}