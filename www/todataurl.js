/*
 *
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

var exec = require('cordova/exec');

var ToDataURL = {

    // Pass width and height of the bitmap data and get DataURL of the image.
    getImageData: function (data, width, height, type, quality) {
        if (typeof quality === 'number') {
            quality *= 100;
        } else {
            quality = 100;
        }

        exec(null, null, "ToDataURL", "getImageData", [data, width, height, type, quality]);
    }

};

(function () {

    var toDataURL = function (type, quality) {
        // var imageData = Array.prototype.slice.call (this.getContext ("2d").getImageData (0, 0, this.width, this.height).data);

        return ToDataURL.getImageData (this.getContext ("2d").getImageData (0, 0, this.width, this.height).data, this.width, this.height, type, quality);
    };

    var _toDataURL = HTMLCanvasElement.prototype.toDataURL;

    HTMLCanvasElement.prototype.toDataURL = function (type, quality) {
        var result = _toDataURL.apply (this, arguments);

        if (result === "data:,") {
            HTMLCanvasElement.prototype.toDataURL = toDataURL;
            return this.toDataURL ();
        } else {
            HTMLCanvasElement.prototype.toDataURL = _toDataURL;
            return result;
        }
    };

}) ();

module.exports = ToDataURL;
