/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 MessagesTest.java is part of BukkitUtilities.

 BukkitUtilities is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BukkitUtilities is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BukkitUtilities. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer;

import java.lang.reflect.Method;
import java.util.*;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.Test;

public class MessagesTest {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("banhammer");

	protected static final String methodNameToBundleKey(String name) {
		String key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
		key = key.replaceAll("_", ".");
		return key;
	}

	@Test public void checkAllMessagesHaveBeenTranslated() throws Exception {
		Collection<String> untranslatedKeys = new ArrayList<>();
		Iterable<Method> interfaceMethods = new HashSet<>(Arrays.asList(BanHammerMessages.class.getMethods()));
		for (Method method : interfaceMethods) {
			String key = methodNameToBundleKey(method.getName());
			if (!RESOURCE_BUNDLE.containsKey(key)) untranslatedKeys.add(key);
		}
		if (!untranslatedKeys.isEmpty()) {
			String keys = Joiner.on(", ").skipNulls().join(untranslatedKeys);
			Assert.fail("Not all messages have been translated! The following keys need to be added: " + keys + ".");
		}
	}

}
