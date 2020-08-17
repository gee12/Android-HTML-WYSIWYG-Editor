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

RE.currentSelection = {
    "startContainer": 0,
    "startOffset": 0,
    "endContainer": 0,
    "endOffset": 0};

RE.editor = document.body;

RE.textChange = function() {
    Android.textChange(/*RE.getText(), */RE.getHtml());
}

// send state of selected text when user click or keyup
RE.stateChange = function() {
    var items = [];
    var node;
    if (window.getSelection) {
        var sel = window.getSelection();
        if (sel.rangeCount > 0) {
//            node = sel.getRangeAt(0).startContainer.parentNode;
            node = sel.getRangeAt(0).commonAncestorContainer;
            // Make sure we have an element rather than a TEXT_NODE
            if (node.nodeType == 3) {
                node = node.parentNode;
            }
        }
    } else if ((sel = document.selection) && sel.type != "Control") {
        node = sel.createRange().parentElement();
    }
    if (window.getComputedStyle(node, "background-color")) {
        items.push(encodeParam("background-color",
            window.getComputedStyle(node, "background-color").getPropertyValue("background-color")));
     }
    if (window.getComputedStyle(node, "color")) {
        items.push(encodeParam("color",
            window.getComputedStyle(node, "color").getPropertyValue("color")));
    }
    var fontSize = document.queryCommandValue("fontSize");
    if (fontSize.length > 0) {
        items.push(encodeParam("font-size", fontSize));
    }
    if (document.queryCommandState("bold")) {
        items.push("bold");
    }
    if (document.queryCommandState("italic")) {
        items.push("italic");
    }
    if (document.queryCommandState("subscript")) {
        items.push("subscript");
    }
    if (document.queryCommandState("superscript")) {
        items.push("superscript");
    }
    if (document.queryCommandState("strikeThrough")) {
        items.push("strikeThrough");
    }
    if (document.queryCommandState("underline")) {
        items.push("underline");
    }
    if (document.queryCommandState("insertOrderedList")) {
        items.push("insertOrderedList");
    } else if (document.queryCommandState("insertUnorderedList")) {
        items.push("insertUnorderedList");
    }
    if (document.queryCommandState("justifyCenter")) {
        items.push(encodeParam("justify", "center"));
    } else if (document.queryCommandState("justifyFull")) {
        items.push(encodeParam("justify", "full"));
    } else if (document.queryCommandState("justifyLeft")) {
        items.push(encodeParam("justify", "left"));
    } else if (document.queryCommandState("justifyRight")) {
        items.push(encodeParam("justify", "right"));
    }
    // ?
    if (document.queryCommandState("createLink")) {
        items.push("createLink");
    }
    var formatBlock = document.queryCommandValue("formatBlock");
    if (formatBlock.length > 0) {
        items.push(formatBlock);
    }

    var formatsAsQuery = items.join("&");
    Android.stateChange(formatsAsQuery/*, RE.getText()*/);
}

function encodeParam(attr) {
//    return encodeURIComponent(attr.toUpperCase());
    return (attr);
}

function encodeParam(attr, value) {
//    return encodeURIComponent(attr.toUpperCase()) + "=" + encodeURIComponent(value.toUpperCase());
    return (attr) + "=" + (value);
}

RE.setHtml = function(contents) {
    RE.editor.innerHTML = decodeURIComponent(contents.replace(/\+/g, "%20"));
}

RE.getHtml = function() {
    return RE.editor.innerHTML;
}

RE.getText = function() {
    return RE.editor.innerText;
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
    document.execCommand("undo", false, null);
}

RE.redo = function() {
    document.execCommand("redo", false, null);
}

RE.moveCursor = function(direction, granularity) {
    var sel = window.getSelection();
    sel.modify("move", (direction > 0) ? "forward" : "backward", granularity);
}

RE.moveSelection = function(direction, granularity) {
    var sel = window.getSelection();
    sel.modify("extend", (direction > 0) ? "forward" : "backward", granularity);
}

RE.selectWord = function() {
    var sel = window.getSelection();
	if (sel.rangeCount > 0) {
         sel.collapse(sel.focusNode, sel.focusOffset);
         sel.modify("move", "forward", "character");
         sel.modify("move", "backward", "word");
         sel.modify("extend", "forward", "word");
    }
}

RE.selectAll = function() {
    document.execCommand("selectAll");
}

RE.copy = function() {
    document.execCommand("copy");
}

RE.cut = function() {
    document.execCommand("cut");
}
/*
RE.paste = function() {
    document.execCommand("paste");
}
}*/

// FIXME:
//  не вставляется многострочнй текст (с разделителями "\n")
RE.pasteTextOnly = function(text) {
    document.execCommand("insertText", false, text);
}

RE.paste = function(html) {
//    var sel = window.getSelection();
    document.execCommand("insertHTML", false, html);
}

RE.forwardDelete = function() {
    document.execCommand("forwardDelete");
}

RE.getSelectedText = function() {
    var sel = window.getSelection();
    return sel.toString();
}

// FIXME:
//  метод не учитывает "родительские" стили, накладываемые на выделение
//  При этом, команда копирования в стандартном ActionMode копирует верно (со стилями)
RE.getSelectedHtml = function() {
    var sel = window.getSelection();
    if (sel.rangeCount > 0) {
        var range = sel.getRangeAt(0);
        var container = document.createElement("div");
        container.appendChild(range.cloneContents());
        return container.innerHTML;
    }
    return "";
}

RE.deleteSelected = function() {
    var sel = window.getSelection();
    if (sel.rangeCount > 0) {
        //var range = sel.getRangeAt(0);
        //range.deleteContents();
	    sel.deleteFromDocument();
    }
}

RE.setBold = function() {
    document.execCommand("bold", false, null);

}

RE.setItalic = function() {
    document.execCommand("italic", false, null);
}

RE.setSubscript = function() {
    document.execCommand("subscript", false, null);
}

RE.setSuperscript = function() {
    document.execCommand("superscript", false, null);
}

RE.setStrikeThrough = function() {
    document.execCommand("strikeThrough", false, null);
}

RE.setUnderline = function() {
    document.execCommand("underline", false, null);
}

RE.setBullets = function() {
    document.execCommand("insertUnorderedList", false, null);
}

RE.setNumbers = function() {
    document.execCommand("insertOrderedList", false, null);
}

RE.setTextColor = function(color) {
    RE.restoreRange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand("foreColor", false, color);
    document.execCommand("styleWithCSS", null, false);
}

RE.setTextBackgroundColor = function(color) {
    RE.restoreRange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand("hiliteColor", false, color);
    document.execCommand("styleWithCSS", null, false);
}

RE.setFontSize = function(fontSize){
    document.execCommand("fontSize", false, fontSize);
}

RE.setHeading = function(heading) {
    document.execCommand("formatBlock", false, "<h"+heading+">");
}

RE.setIndent = function() {
    document.execCommand("indent", false, null);
}

RE.setOutdent = function() {
    document.execCommand("outdent", false, null);
}

RE.setJustifyLeft = function() {
    document.execCommand("justifyLeft", false, null);
}

RE.setJustifyCenter = function() {
    document.execCommand("justifyCenter", false, null);
}

RE.setJustifyRight = function() {
    document.execCommand("justifyRight", false, null);
}

RE.setBlockquote = function() {
    document.execCommand("formatBlock", false, "<blockquote>");
}

RE.setCode = function() {
    document.execCommand("formatBlock", false, "<pre>");
}

RE.removeFormat = function() {
    document.execCommand("removeFormat", false, null);
}

RE.insertLine = function() {
    document.execCommand("insertHorizontalRule", false, null);
}

RE.insertImage = function(url, alt) {
    var html = '<img src="' + url + '"' + (alt != null) ? ' alt="' + alt + '"' : '' + '/>';
    RE.insertHTML(html);
}

RE.insertImage = function(url, width, height) {
    var html = '<img src="' + url + '" width="' + width + ' height="' + height + '"/><br>';
    RE.insertHTML(html);
}

RE.insertYoutubeVideo = function(url) {
    var html = '<iframe id="player" type="text/html" src="https://www.youtube.com/embed/' + url
        + '?enablejsapi=1" frameborder="0"></iframe><br>'
    RE.insertHTML(html);
}

RE.insertHTML = function(html) {
    RE.restoreRange();
    document.execCommand("insertHTML", false, html);
}

// create new link (with url and text) or converts exist text to a link
RE.insertLink = function(url, text) {
    RE.restoreRange();
    var sel = document.getSelection();
    if (sel.toString().length == 0) {
        var html = "<a href='"+url+"'>"+text+"</a>";
        document.execCommand("insertHTML", false, html);
    } else if (sel.rangeCount > 0) {
       var el = document.createElement("a");
       el.setAttribute("href", url);
       if (text != null) {
            el.setAttribute("title", text);
       }
       var range = sel.getRangeAt(0).cloneRange();
       range.surroundContents(el);
       sel.removeAllRanges();
       sel.addRange(range);
    }
    RE.textChange();
}


RE.createLink = function(url, title) {
    document.execCommand("createLink", false, url);
}

RE.removeLink = function() {
    document.execCommand("unlink", false, null);
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

RE.insertTodo = function(text) {
    var html = '<input type="checkbox" name="'+ text +'" value="'+ text +'"/> &nbsp;';
    document.execCommand("insertHTML", false, html);
}

RE.prepareInsert = function() {
    RE.saveRange();
}

RE.saveRange = function(){
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

RE.restoreRange = function(){
    var selection = window.getSelection();
    selection.removeAllRanges();
    var range = document.createRange();
    range.setStart(RE.currentSelection.startContainer, RE.currentSelection.startOffset);
    range.setEnd(RE.currentSelection.endContainer, RE.currentSelection.endOffset);
    selection.addRange(range);
}

RE.focus = function() {
    RE.editor.focus();
}

RE.focusToEnd = function() {
    var range = document.createRange();
    range.selectNodeContents(RE.editor);
    range.collapse(false);
    var selection = window.getSelection();
    selection.removeAllRanges();
    selection.addRange(range);
    RE.editor.focus();
}

RE.clearFocus = function() {
    RE.editor.blur();
}

RE.clearAndFocusEditor = function() {
    var range = document.createRange();
    range.selectNodeContents(RE.editor);
    range.collapse(false);
    var selection = window.getSelection();
    if (selection.toString().length == 0) {
        RE.editor.blur();
        RE.editor.focus();
    }
}

// event listeners
document.addEventListener("selectionchange", RE.saveRange);

RE.editor.addEventListener("input", RE.textChange);
RE.editor.addEventListener("keyup", function(e) {
    var KEY_LEFT = 37, KEY_RIGHT = 39;
    if (e.which == KEY_LEFT || e.which == KEY_RIGHT) {
        RE.statechange();
    }
});
RE.editor.addEventListener("click", RE.stateChange);
