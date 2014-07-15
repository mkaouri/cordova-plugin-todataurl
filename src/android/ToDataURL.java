/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.apache.cordova.todataurl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

public class ToDataURL extends CordovaPlugin {
	private static final String LOG_TAG = "ToDataURL";

	/**
	 * Sets the context of the Command. This can then be used to do things like
	 * get file paths associated with the Activity.
	 *
	 * @param cordova The context of the main Activity.
	 * @param webView The CordovaWebView Cordova is running in.
	 */
	@Override
	public void initialize (CordovaInterface cordova, CordovaWebView webView) {
		Log.v(LOG_TAG, "ToDataURL: initialization");

		super.initialize (cordova, webView);
	}

	/**
	 * Executes the request and returns PluginResult.
	 *
	 * @param action			The action to execute.
	 * @param args			  JSONArry of arguments for the plugin.
	 * @param callbackContext   The callback id used when calling back into JavaScript.
	 * @return				  True if the action was valid, false otherwise.
	 */
	@Override
	public boolean execute (String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
		Log.v(LOG_TAG, "Executing action: " + action);

		if ("getImageData".equals (action)) {
			try {
				// String encodedData = args.getString (0).replaceFirst ("^data:image/(png|jpg|jpeg);base64,", "");
				// Log.i(LOG_TAG, "getImageData[" + encodedData.length () + "] = " + encodedData);

				byte[] data = Base64.decode (args.getString (0).replaceFirst ("^data:image/(png|jpg|jpeg);base64,", ""), Base64.DEFAULT);// args.getArrayBuffer (0);
				/*
				int width = args.getInt (1);
				int height = args.getInt (2);
				*/
				String type = args.optString (1, "image/png");
				int quality = args.optInt (2, 100);
				int orientation = args.optInt (3, 0);

				// Log.i(LOG_TAG, "getImageData[" + type + "][" + quality + "] = " + width + "x" + height);
				// Log.i(LOG_TAG, "getImageData[" + data.length + "] = " + data);

				Bitmap bmp = BitmapFactory.decodeByteArray (data, 0, data.length);
				/*
				bmp = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
				bmp.copyPixelsFromBuffer (ByteBuffer.wrap (data));
				*/
				// Log.i(LOG_TAG, "getImageData::bmp = " + bmp);

				try
				{
					final Matrix bitmapMatrix = new Matrix ();

					switch (orientation)
					{
						case 1:                                                                                       break;  // top left
						case 2:                                                  bitmapMatrix.postScale (-1, 1);      break;  // top right
						case 3:         bitmapMatrix.postRotate (180);                                                break;  // bottom right
						case 4:         bitmapMatrix.postRotate (180);           bitmapMatrix.postScale (-1, 1);      break;  // bottom left
						case 5:         bitmapMatrix.postRotate (90);            bitmapMatrix.postScale (-1, 1);      break;  // left top
						case 6:         bitmapMatrix.postRotate (90);                                                 break;  // right top
						case 7:         bitmapMatrix.postRotate (270);           bitmapMatrix.postScale (-1, 1);      break;  // right bottom
						case 8:         bitmapMatrix.postRotate (270);                                                break;  // left bottom
						default:                                                                                      break;  // Unknown
					}

					// Create new bitmap.
					bmp = Bitmap.createBitmap (bmp, 0, 0, bmp.getWidth (), bmp.getHeight (), bitmapMatrix, false);

					ByteArrayOutputStream out = new ByteArrayOutputStream ();
					bmp.compress ((type.endsWith ("jpeg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG), quality, out);

					String dataURL = "data:" + type + ";base64," + Base64.encodeToString (out.toByteArray (), 0);
					// Log.i(LOG_TAG, "getImageData::dataURL = " + dataURL);

					PluginResult r = new PluginResult(PluginResult.Status.OK, dataURL);
					callbackContext.sendPluginResult(r);

					return true;
				}
				catch (Exception e)
				{
					// TODO: handle exception
					callbackContext.error ("Create Bitmap Matrix");

					PluginResult r = new PluginResult(PluginResult.Status.ERROR);
					callbackContext.sendPluginResult(r);

					return true;
				}
			}
			catch (IllegalArgumentException e) {
				callbackContext.error ("Illegal Argument Exception");

				PluginResult r = new PluginResult(PluginResult.Status.ERROR);
				callbackContext.sendPluginResult(r);

				return true;
			}
			/*
			PluginResult r = new PluginResult (PluginResult.Status.NO_RESULT);
			r.setKeepCallback (true);
			callbackContext.sendPluginResult (r);

			return true;
			*/
		}

		return false;
	}
}
