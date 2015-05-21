"use strict";

var messageStruct = function (author, text) {
                    return {
                        id: 0,
						author: author,
						text: text };
                    },
    appState      = {
                    mainUrl : 'chat',
                    token : 'TN11EN',
                    version : 0 },
	messageList   = [],
    editingMessage;

function run() {
    var chatWindow = document.getElementsByClassName("chatWindow")[0],
        loginWindow = document.getElementById("loginWindow");
    chatWindow.addEventListener("click", delegateEvent);
    chatWindow.addEventListener("keydown", delegateEvent);
	loginWindow.addEventListener("click", delegateEvent);
    loginWindow.addEventListener("keydown", delegateEvent);
}

function delegateEvent(eventObj) {
    if ((eventObj.target.getAttribute("id") === "sendButton") || 
		((eventObj.keyCode === 13) && (eventObj.target.getAttribute("id") === "textBox"))
	   ) {
        onSendButtonClick();
    }
    else if (eventObj.target.getAttribute("class") === "editMessageButton icon") {
        onEditMessageButtonClick(eventObj);
    }
    else if (eventObj.target.getAttribute("class") === "deleteMessageButton icon") {
        onDeleteMessageButtonClick(eventObj);
    }
    else if (eventObj.target.getAttribute("id") === "loginButton") {
        onLoginButtonClick();   
    }
    else if (eventObj.target.getAttribute("id") === "editLoginButton") {
        onEditLoginButtonClick();
    }
    else if (eventObj.target.getAttribute("id") === "logoutButton") {
        onLogoutButtonClick();
    }
    else if ((eventObj.target.getAttribute("id") === "loginWindowButton") || 
			 ((eventObj.keyCode === 13) && (eventObj.target.getAttribute("id") === "loginWindowInput"))
			) {
        onLoginWindowButtonClick();
    }
    else if (eventObj.target.getAttribute("id") === "dismissLoginWindowButton") {
        onDismissLoginWindowButtonClick();
    }
}

function onSendButtonClick() {
    var messageText = document.getElementById("textBox"), 
		username = document.getElementById("username"),
		message = messageStruct(username.innerText, messageText.innerText),
        sendButton = document.getElementById("sendButton"),
        i;
    if (sendButton.innerHTML == "Send") {
        addMessage(message);
        messageText.innerHTML = "";
        return;
    } else {
        var id = editingMessage.attributes["id"].value;
        for (i = 0; i < messageList.length; i++) {
            if (messageList[i].id === id) {
                editingMessage.childNodes[0].childNodes[1].innerHTML = messageText.innerHTML;
                updateMessageList(messageText.innerHTML, messageList[i]);
                messageText.innerHTML = "";
                sendButton.innerHTML = "Send";
            }    
        }
    }
}

function onEditMessageButtonClick(eventObj) {
    var user = document.getElementById("username");
    if (user.innerHTML + ":&nbsp;" != eventObj.target.parentNode.childNodes[0].childNodes[0].innerHTML) {
        alert("This is not your message.");
        return;
    }
        
    var id = eventObj.target.parentElement.attributes["id"].value,
		parentMessage = eventObj.target.parentNode,
		oldMessage = parentMessage.getElementsByClassName("text")[0],
		editButton = document.getElementById("sendButton"),
		messageArea = document.getElementById("textBox"),
        i;
    for (i = 0; i < messageList.length; i++) {
        if (messageList[i].id === id) {
            editingMessage = parentMessage;
            messageArea.innerText = oldMessage.innerText;
            editButton.innerHTML = "Edit";
            return;
        }
    }
}

function onDeleteMessageButtonClick(eventObj) {
    var parentMessage = eventObj.target.parentNode,
        id = parentMessage.attributes["id"].value,
        i,
		sendButton = document.getElementById("sendButton"),
        user = document.getElementById("username");
    if (user.innerHTML + ":&nbsp;" != eventObj.target.parentNode.childNodes[0].childNodes[0].innerHTML) {
        alert("This is not your message.");
        return;
    }
	if (sendButton.innerHTML == "Edit") {
		return;
	}
    $("messageBox").remove(eventObj.target.parentNode);
    for (i = 0; i < messageList.length; i++) {
        if (messageList[i].id === id) {
            
            $.ajax({
                url: appState.mainUrl,
                type: "DELETE",
                data: JSON.stringify(messageList[i]),
                success: indicatorOn,
                error: indicatorOff
            });
			messageList.splice(i, 1);
            return;
        }
    }
}

function onLoginButtonClick() {
	$("#loginWindowBackground").fadeIn(300);
}

function onEditLoginButtonClick() {
    $("#loginWindowButton").text("Confirm");
    $("#loginWindowBackground").fadeIn(300);
}

function onLogoutButtonClick() {
    $("#username").text("");
	hideAll();
}

function onLoginWindowButtonClick() {
    var login = document.getElementById("loginWindowInput").innerText;
    if (login) {
        addLogin(login);
		revealAll();
    }
}

function onDismissLoginWindowButtonClick () {
    $("#loginWindowBackground").fadeOut(300);
}

function restoreFromServer() { 
    $.ajax({
        url: appState.mainUrl + "?token=" + appState.token + "&version=" + appState.version,
        type: "GET",
        success: function(result, textStatus) {
            if (textStatus === "success") {
                console.assert(result != null);
                appState.token = result.token;
                if (result.version == appState.version) {
                    createAllMessages(result.messages);
                } else {
                    appState.version = result.version;
                    clearAllMessages();
                    createAllMessages(result.messages);
                }
                indicatorOn();
            }
        },
        error: indicatorOff,
        complete: restoreFromServer
    });
}

function createAllMessages(allMessages) {
    var i;
    if (allMessages && allMessages.length) {
        for (i = 0; i < allMessages.length; i++) {
			messageList.push(allMessages[i]);
            addMessageInternal(allMessages[i]);
        }
    }
}

function clearAllMessages() {
    var messageBox = document.getElementById("messageBox");
    messageList.splice(0, messageList.length);
    while (messageBox.firstChild) {
        messageBox.removeChild(messageBox.firstChild);
    }
}

function revealAll() {	
	$("#hiddenUserBox").fadeOut(300);
	$("#hiddenMessageBox").fadeOut(300);
	$("#hiddenTextBox").fadeOut(300);
    $("#loginWindowBackground").fadeOut(300);
    
    $("#loginButton").hide();
    $("#logoutButton").show();
    $("#editLoginButton").show();
}

function hideAll() {
	$("#hiddenUserBox").fadeIn(300);
	$("#hiddenMessageBox").fadeIn(300);
	$("#hiddenTextBox").fadeIn(300);
    $("#loginWindowInput").text("");
	
    $("#loginButton").show();
    $("#logoutButton").hide();
    $("#editLoginButton").hide();
}

function addMessage(message) {
    if (!message.text) {
		return;
	}
    
    $.ajax({
        url: appState.mainUrl,
        type: "POST",
        data: JSON.stringify(message),
        success: indicatorOn,
        error: indicatorOff
    });
}
    
function addMessageInternal(message) {
    var newMessage = createMessage(message.author, message.text),
        messages = document.getElementById("messageBox");
    newMessage.id = message.id;
	messages.appendChild(newMessage);
}

function updateMessageList(newMessage, messageList_) {
    messageList_.text = newMessage;
    
    $.ajax({
        url: appState.mainUrl,
        type: "PUT",
        data: JSON.stringify(messageList_),
        success: indicatorOn,
        error: indicatorOff
    });
}

function createMessage(username, textMessage) {
    var message = document.createElement("div"),
        newMessage = document.createElement("div");
    
    $(message).append("<span class='user'>" + username + ":&nbsp" + "</span>");
    $(message).append("<span class='text'>" + textMessage + "</span>");
    $(newMessage).attr("class", "message");
    $(newMessage).append(message);
    $(newMessage).append("<img class='editMessageButton icon' src='resources/css/images/edit.png'>");
    $(newMessage).append("<img class='deleteMessageButton icon' src='resources/css/images/trash.png'>");
    
    return newMessage;
}

function addLogin(value) {
    if (!value) {
		return;
	}
    restoreFromServer();
    var username = document.getElementById("username");
	username.style.display = "block";
    username.innerHTML = value;
}

function indicatorOn() {	
	$("#serverIndicator").animate({"color":"#c2ffe3", "backgroundColor": "#58b98c"}, 350);
}

function indicatorOff() {
	$("#serverIndicator").animate({"color":"#ffc2c2", "backgroundColor": "#b95c58"}, 350);
}