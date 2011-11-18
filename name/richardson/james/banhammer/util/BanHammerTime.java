/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanHammerTime.java is part of BanHammer.
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
package name.richardson.james.banhammer.util;

public class BanHammerTime {

  public final static long HOURS = 24;
  public final static long MINUTES = 60;
  public final static long ONE_SECOND = 1000;
  public final static long ONE_MINUTE = ONE_SECOND * 60;
  public final static long ONE_HOUR = ONE_MINUTE * 60;
  public final static long ONE_DAY = ONE_HOUR * 24;
  public final static long SECONDS = 60;

  /**
   * converts time (in milliseconds) to human-readable format
   * "<w> days, <x> hours, <y> minutes and (z) seconds"
   */
  public static String millisToLongDHMS(long duration) {
    StringBuffer res = new StringBuffer();
    long temp = 0;
    if (duration >= ONE_SECOND) {
      temp = duration / ONE_DAY;
      if (temp > 0) {
        duration -= temp * ONE_DAY;
        res.append(temp).append(" day").append(temp > 1 ? "s" : "").append(duration >= ONE_MINUTE ? ", " : "");
      }

      temp = duration / ONE_HOUR;
      if (temp > 0) {
        duration -= temp * ONE_HOUR;
        res.append(temp).append(" hour").append(temp > 1 ? "s" : "").append(duration >= ONE_MINUTE ? ", " : "");
      }

      temp = duration / ONE_MINUTE;
      if (temp > 0) {
        duration -= temp * ONE_MINUTE;
        res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
      }

      if (!res.toString().equals("") && duration >= ONE_SECOND)
        res.append(" and ");

      temp = duration / ONE_SECOND;
      if (temp > 0)
        res.append(temp).append(" second").append(temp > 1 ? "s" : "");
      return res.toString();
    } else return "0 second";
  }
}
