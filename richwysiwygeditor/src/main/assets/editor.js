/**
 * Основная масса этого кода принадлежит Wasabeef.

 * Copyright (C) 2017 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var RE = {};
var backup_color;

RE.currentSelection = {
    "startContainer": 0,
    "startOffset": 0,
    "endContainer": 0,
    "endOffset": 0};

//RE.editor = document.getElementById('editor');
//RE.editor = document.documentElement;
RE.editor = document.body;

document.addEventListener("selectionchange", function() { RE.backuprange(); });

// Initializations
RE.callback = function() {
    var re_callback = "re-callback://" + encodeURI(RE.getHtml());
    // window.location.href = "re-callback://" + encodeURI(RE.getHtml());

    var items = [];

    var parentNode = window.getSelection().getRangeAt(0).startContainer.parentNode;
    if (window.getComputedStyle(parentNode, "background-color")) {
//        items.push('background_color_'
        items.push('background_color:'
            + window.getComputedStyle(parentNode,"background-color").getPropertyValue('background-color'));
    }
    if (window.getComputedStyle(parentNode, "color")) {
//         items.push('font_color_'
         items.push('text_color:'
            + window.getComputedStyle(parentNode, "color").getPropertyValue('color'));
    }
    if (document.queryCommandState('bold')) {
        items.push('bold');
    }
    if (document.queryCommandState('italic')) {
        items.push('italic');
    }
    if (document.queryCommandState('subscript')) {
        items.push('subscript');
    }
    if (document.queryCommandState('superscript')) {
        items.push('superscript');
    }
    if (document.queryCommandState('strikeThrough')) {
        items.push('strikeThrough');
    }
    if (document.queryCommandState('underline')) {
        items.push('underline');
    }
    if (document.queryCommandState('insertOrderedList')) {
        items.push('ordered_list');
    }
    if (document.queryCommandState('insertUnorderedList')) {
        items.push('unordered_list');
    }
    if (document.queryCommandState('justifyCenter')) {
        items.push('justify_center');
    }
    if (document.queryCommandState('justifyFull')) {
        items.push('justify_full');
    }
    if (document.queryCommandState('justifyLeft')) {
        items.push('justify_left');
    }
    if (document.queryCommandState('justifyRight')) {
        items.push('justify_right');
    }
//    if (document.queryCommandState('insertHorizontalRule')) {
//        items.push('horizontalRule');
//    }
    var formatBlock = document.queryCommandValue('formatBlock');
    if (formatBlock.length > 0) {
        items.push(formatBlock);
    }

    window.location.href = re_callback + "re-state://" + encodeURI(items.join(';'));

}

//RE.setHtml = function(contents) {
//    RE.editor.innerHTML = decodeURIComponent(contents.replace(/\+/g, '%20'));
//}

RE.getHtml = function() {
    return RE.editor.innerHTML;
//    return RE.editor.outerHTML;
}

RE.getText = function() {
    return RE.editor.innerText;
//    return RE.editor.outerText;
}

RE.setBaseTextColor = function(color) {
    RE.editor.style.color  = color;
}

RE.setBaseFontSize = function(size) {
    RE.editor.style.fontSize = size;
}

RE.setPadding = function(left, top, right, bottom) {
  RE.editor.style.paddingLeft = left;
  RE.editor.style.paddingTop = top;
  RE.editor.style.paddingRight = right;
  RE.editor.style.paddingBottom = bottom;
}

RE.setBackgroundColor = function(color) {
    document.body.style.backgroundColor = color;
}

RE.setBackgroundImage = function(image) {
    RE.editor.style.backgroundImage = image;
}

RE.setWidth = function(size) {
    RE.editor.style.minWidth = size;
}

RE.setHeight = function(size) {
    RE.editor.style.height = size;
}

RE.setTextAlign = function(align) {
    RE.editor.style.textAlign = align;
}

RE.setVerticalAlign = function(align) {
    RE.editor.style.verticalAlign = align;
}

RE.setPlaceholder = function(placeholder) {
    RE.editor.setAttribute("placeholder", placeholder);
}

RE.setInputEnabled = function(inputEnabled) {
    // document.designMode
    RE.editor.contentEditable = String(inputEnabled);
}

RE.getInputEnabled = function() {
    return RE.editor.contentEditable;
}

RE.undo = function() {
    document.execCommand('undo', false, null);
}

RE.redo = function() {
    document.execCommand('redo', false, null);
}

RE.setBold = function() {
    document.execCommand('bold', false, null);

}

RE.setItalic = function() {
    document.execCommand('italic', false, null);
}

RE.setSubscript = function() {
    document.execCommand('subscript', false, null);
}

RE.setSuperscript = function() {
    document.execCommand('superscript', false, null);
}

RE.setStrikeThrough = function() {
    document.execCommand('strikeThrough', false, null);
}

RE.setUnderline = function() {
    document.execCommand('underline', false, null);
}

RE.setBullets = function() {
    document.execCommand('insertUnorderedList', false, null);
}

RE.setNumbers = function() {
    document.execCommand('insertOrderedList', false, null);
}

RE.setTextColor = function(color) {
    RE.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('foreColor', false, color);
    document.execCommand("styleWithCSS", null, false);
}

RE.setTextBackgroundColor = function(color) {
    RE.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('hiliteColor', false, color);
    document.execCommand("styleWithCSS", null, false);
}

RE.setFontSize = function(fontSize){
    document.execCommand("fontSize", false, fontSize);
}

RE.setHeading = function(heading) {
    document.execCommand('formatBlock', false, '<h'+heading+'>');
}

RE.setIndent = function() {
    document.execCommand('indent', false, null);
}

RE.setOutdent = function() {
    document.execCommand('outdent', false, null);
}

RE.setJustifyLeft = function() {
    document.execCommand('justifyLeft', false, null);
}

RE.setJustifyCenter = function() {
    document.execCommand('justifyCenter', false, null);
}

RE.setJustifyRight = function() {
    document.execCommand('justifyRight', false, null);
}

RE.setBlockquote = function() {
    document.execCommand('formatBlock', false, '<blockquote>');
}

RE.insertLine = function() {
//    var html = '<hr>';
//    RE.insertHTML(html);
    document.execCommand('insertHorizontalRule', false, null);
}

RE.insertImage = function(url, alt) {
    var html = '<img src="' + url + '" alt="' + alt + '" /><br><br>';
    RE.insertHTML(html);
}

RE.insertYoutubeVideo = function(url) {
    var html = '<iframe id="player" type="text/html" src="https://www.youtube.com/embed/' + url
        + '?enablejsapi=1" frameborder="0"></iframe><br><br>'
    RE.insertHTML(html);
}

RE.insertHTML = function(html) {
    RE.restorerange();
    document.execCommand('insertHTML', false, html);
}

RE.insertLink = function(url, title) {
    RE.restorerange();
    var sel = document.getSelection();
    if (sel.toString().length == 0) {
        document.execCommand("insertHTML",false,"<a href='"+url+"'>"+title+"</a>");
    } else if (sel.rangeCount) {
       var el = document.createElement("a");
       el.setAttribute("href", url);
       el.setAttribute("title", title);

       var range = sel.getRangeAt(0).cloneRange();
       range.surroundContents(el);
       sel.removeAllRanges();
       sel.addRange(range);
   }
    RE.callback();
}

RE.createLink = function(url, title) {
    document.execCommand('createLink', false, url);
}

RE.removeLink = function() {
    document.execCommand('unlink', false, null);
}

RE.insertCSS = function(cssFile) {
    var head  = document.getElementsByTagName("head")[0];
    var link  = document.createElement("link");
    link.rel  = "stylesheet";
    link.type = "text/css";
    link.href = cssFile;
    link.media = "all";
    head.appendChild(link);
}

RE.setTodo = function(text) {
    var html = '<input type="checkbox" name="'+ text +'" value="'+ text +'"/> &nbsp;';
    document.execCommand('insertHTML', false, html);
}

RE.prepareInsert = function() {
    RE.backuprange();
}

RE.backuprange = function(){
    var selection = window.getSelection();
    if (selection.rangeCount > 0) {
      var range = selection.getRangeAt(0);
      RE.currentSelection = {
          "startContainer": range.startContainer,
          "startOffset": range.startOffset,
          "endContainer": range.endContainer,
          "endOffset": range.endOffset};
    }
}

RE.restorerange = function(){
    var selection = window.getSelection();
    selection.removeAllRanges();
    var range = document.createRange();
    range.setStart(RE.currentSelection.startContainer, RE.currentSelection.startOffset);
    range.setEnd(RE.currentSelection.endContainer, RE.currentSelection.endOffset);
    selection.addRange(range);
}

RE.enabledEditingItems = function(e) {
    var items = [];

    var parentNode = window.getSelection().getRangeAt(0).startContainer.parentNode;
    if (window.getComputedStyle(parentNode, "background-color")) {
//        items.push('background_color_'
        items.push('background_color:'
            + window.getComputedStyle(parentNode, "background-color").getPropertyValue('background-color'));
     }
    if (window.getComputedStyle(parentNode, "color")) {
//         items.push('font_color_'
         items.push('text_color:'
            + window.getComputedStyle(parentNode, "color").getPropertyValue('color'));
     }
    if (document.queryCommandState('bold')) {
        items.push('bold');
    }
    if (document.queryCommandState('italic')) {
        items.push('italic');
    }
    if (document.queryCommandState('subscript')) {
        items.push('subscript');
    }
    if (document.queryCommandState('superscript')) {
        items.push('superscript');
    }
    if (document.queryCommandState('strikeThrough')) {
        items.push('strikeThrough');
    }
    if (document.queryCommandState('underline')) {
        items.push('underline');
    }
    if (document.queryCommandState('insertOrderedList')) {
        items.push('ordered_list');
    }
    if (document.queryCommandState('insertUnorderedList')) {
        items.push('unordered_list');
    }
    if (document.queryCommandState('justifyCenter')) {
        items.push('justify_center');
    }
    if (document.queryCommandState('justifyFull')) {
        items.push('justify_full');
    }
    if (document.queryCommandState('justifyLeft')) {
        items.push('justify_left');
    }
    if (document.queryCommandState('justifyRight')) {
        items.push('justify_right');
    }
//    if (document.queryCommandState('insertHorizontalRule')) {
//        items.push('horizontalRule');
//    }
    var formatBlock = document.queryCommandValue('formatBlock');
    if (formatBlock.length > 0) {
        items.push(formatBlock);
    }

    window.location.href = "re-state://" + encodeURI(items.join(';'));
}

RE.focus = function() {
    var range = document.createRange();
    range.selectNodeContents(RE.editor);
    range.collapse(false);
    var selection = window.getSelection();
    selection.removeAllRanges();
    selection.addRange(range);
    RE.editor.focus();
}

RE.blurFocus = function() {
    RE.editor.blur();
}

RE.clearAndFocusEditor = function() {
    var range = document.createRange();
    range.selectNodeContents(RE.editor);
    range.collapse(false);
    var selection = window.getSelection();
    if(selection.toString().length == 0){
        RE.editor.blur();
        RE.editor.focus();
     }
}

RE.removeFormat = function() {
    document.execCommand('removeFormat', false, null);
}

// Event Listeners
RE.editor.addEventListener("input", RE.callback);
RE.editor.addEventListener("keyup", function(e) {
    var KEY_LEFT = 37, KEY_RIGHT = 39;
    if (e.which == KEY_LEFT || e.which == KEY_RIGHT) {
        RE.enabledEditingItems(e);
    }
});
RE.editor.addEventListener("click", RE.enabledEditingItems);
