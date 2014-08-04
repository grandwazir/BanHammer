/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 BanHammerMessagesCreator.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer;

import java.lang.reflect.Proxy;
import java.util.ResourceBundle;

import name.richardson.james.bukkit.utilities.localisation.BasicMessageHandler;
import name.richardson.james.bukkit.utilities.localisation.ColouredMessageHandler;

public final class BanHammerMessagesCreator {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("banhammer");
	private static final BanHammerMessages PLAIN_MESSAGES = (BanHammerMessages) Proxy.newProxyInstance(BanHammerMessages.class.getClassLoader(), new Class[]{BanHammerMessages.class}, new BasicMessageHandler(RESOURCE_BUNDLE));
	private static final BanHammerMessages COLOURED_MESSAGES = (BanHammerMessages) Proxy.newProxyInstance(BanHammerMessages.class.getClassLoader(), new Class[]{BanHammerMessages.class}, new ColouredMessageHandler(RESOURCE_BUNDLE));

	private BanHammerMessagesCreator() {}

	public static BanHammerMessages getColouredMessages() {
		return COLOURED_MESSAGES;
	}

	public static BanHammerMessages getMessages() {
		return PLAIN_MESSAGES;
	}
}
