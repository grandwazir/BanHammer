/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * NoMatchingPlayerException.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.banhammer.exceptions;

public class NoMatchingPlayerException extends Exception {


  private static final long serialVersionUID = 430445873851296870L;

  public NoMatchingPlayerException(String format) {
    super(format);
  }

}
