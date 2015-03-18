/*
 * Copyright (c) 2015 OpenSilk Productions LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package syncthing.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by drew on 3/1/15.
 */
public class GUIConfig implements Serializable {
    private static final long serialVersionUID = -9130937931611793075L;
    @SerializedName("Enabled")public boolean enabled = true;
    @SerializedName("Address")public String address = "127.0.0.1:8080";
    @SerializedName("User")public String user;
    @SerializedName("Password")public String password;
    @SerializedName("UseTLS")public boolean useTLS;
    @SerializedName("APIKey")public String apiKey;
}
