/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 MessagesFactory.java is part of BanHammer.

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

public final class MessagesFactory {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("banhammer");
	private static final Messages PLAIN_MESSAGES = (Messages) Proxy.newProxyInstance(Messages.class.getClassLoader(), new Class[]{Messages.class}, new BasicMessageHandler(RESOURCE_BUNDLE));
	private static final Messages COLOURED_MESSAGES = (Messages) Proxy.newProxyInstance(Messages.class.getClassLoader(), new Class[]{Messages.class}, new ColouredMessageHandler(RESOURCE_BUNDLE));

	private MessagesFactory() {}

	public static Messages getColouredMessages() {
		return COLOURED_MESSAGES;
	}

	public static Messages getMessages() {
		return PLAIN_MESSAGES;
	}
}
