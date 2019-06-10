// SDK загружено
var initialized = false;
var currentCall;
var voxLogin;
var voxPassword;
var callToPhone;
var micMuted;
var webCallToClientId;
var commonWebCallId;
var allowed;
var iconCallBackCall;
var iconWebCall;
var elBtnMic;
var btnCallOff;
const sdk = VoxImplant.getInstance();
sdk.on(VoxImplant.Events.SDKReady, onSdkReady);
sdk.on(VoxImplant.Events.ConnectionEstablished, onConnectionEstablished);
sdk.on(VoxImplant.Events.AuthResult, onAuthResult);

//callback
function callToClient(userPhone, clientPhone) {
    console.log("TRY TO CALL");
    var url = "/user/rest/call/voximplant";
    var formData = {
        from: userPhone,
        to: clientPhone
    };
    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        success: function () {
            console.log("PROCESS CALL");
            callToolControl('callBackToClient');
        },
        error: function (error) {
            console.log("ERROR CALL");
            console.log(error);
        }
    });
}

//web call to client via Voximplant SDK
function webCallToClient(clientPhone) {
    console.log("Trying to call to client using Voximplant SDK");
    var url = "/user/rest/call/toClient";
    callToPhone = clientPhone;
    var formData = {
        to: callToPhone
    };
    micMuted = false;
    $.ajax({
        async: false,
        type: 'post',
        url: url,
        data: formData,
        success: function(callRecord) {
            console.log("PROCESS WEBCALL");
            webCallToClientId = callRecord.id;
            commonWebCallId = 0;
            allowed = true;
            callToolControl('showWebCallClientTools');
        },
        error: function (error) {
            console.log("ERROR WEBCALL");
            console.log(error);
            allowed = false;
        }
    });
    if (allowed) {
        getCredentials();
        startCall();
    }
}

// common webcall (any number) via Voximplant SDK
function commonWebCall(phoneNumber) {
    console.log("Trying to call at number which is not client");
    var url = "/user/rest/call/common";
    callToPhone = phoneNumber;
    var formData = {
        to: callToPhone
    };
    micMuted = false;
    $.ajax({
        async: false,
        type: 'post',
        url: url,
        data: formData,
        success: function(callRecord) {
            console.log("PROCESS WEBCALL");
            webCallToClientId = 0;
            commonWebCallId = callRecord.id;
            allowed = true;
            callToolControl('showWebCallClientTools');
        },
        error: function (error) {
            console.log("ERROR WEBCALL");
            console.log(error);
            allowed = false;
        }
    });
    if (allowed) {
        getCredentials();
        startCall();
    }
}

//getting voximplant login and password from server
function getCredentials() {
    var credentialsUrl = "/user/rest/call/voximplantCredentials";
    $.ajax({
        async: false,
        type: 'get',
        url: credentialsUrl,
        success: function (credentials) {
            var arr = credentials.split(",");
            voxLogin = arr[0];
            voxPassword = arr[1];
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//voximplant handlers
function onSdkReady() {
    sdk.connect(false);
    console.log('sdk is initialized');
}

function onConnectionEstablished() {
    console.log('It is connected to vox cloud');
    if (sdk.getClientState() !== "LOGGED_SUCCESS") {
        sdk.requestOneTimeLoginKey(voxLogin)
        console.log('request for authentication');
    } else {
        console.log('already authorize.');
        startCall();
    }
}

function onAuthResult(eAuth) {
    if (eAuth.result) {
        initialized = true;
        console.log('Athentication successfull');
        startCall();
    } else {
        if (eAuth.code == 302) {
            console.log("generating token ... ");
            $.post('/user/rest/call/calcKey', {
                key: eAuth.key
            }, token => {
                sdk.loginWithOneTimeKey(voxLogin, token);
        }, 'text');
        } else {
            console.log("Athentication is unsuccessful with code " + eAuth.code);
        }
    }
}

function startCall() {
    if (!initialized) {
        sdk.init({
            micRequired: true,
            videoSupport: false,
            progressTone: true
        });
    }
    if (initialized) {
        console.log('Starting call .... ');
        try {
            currentCall = sdk.call({number: callToPhone, customData: webCallToClientId + "," + commonWebCallId});
            currentCall.on(VoxImplant.CallEvents.Connected, () => {
                console.log('You can hear audio from the cloud');
            });
            currentCall.on(VoxImplant.CallEvents.Failed, (e) => {
                console.log(`Call failed with the ${e.code} error`);
            stopCall();
            callToolControl('default');
            });
            currentCall.on(VoxImplant.CallEvents.Disconnected, () => {
                console.log('The call has ended');
            stopCall();
            callToolControl('default');
            });
        }
        catch (err) {
            console.log(err);
        }
    }
}

//call hangup voximplant
$(document).on('click', '.web-call-off', stopCall);

function stopCall() {
    console.log("Client-state: " + sdk.getClientState());
    if (currentCall && currentCall.state() != "ENDED") {
        currentCall.hangup();
    }
    callToolControl('default');
}

//mute microphone voximplant
$(document).on('click', '.web-call-mic-off', function() {
    if (currentCall && currentCall.active()) {
        if (micMuted) {
            currentCall.unmuteMicrophone();
            callToolControl('unmuteMicrophone');
        } else {
            currentCall.muteMicrophone();
            callToolControl('muteMicrophone');
        };
        micMuted = !micMuted;
    };
});

// Функция управления отображением кнопок IP-телефонии. Во встраиваемом файле соответствующие кнопки должны быть соответствующих классов.
// Например: class="btn btn-default btn-lg call-to-client" - заканчивается на "call-to-client" (см. обращения селекторов)
function callToolControl(operation) {
    iconCallBackCall = $(".callback-call");
    iconWebCall = $(".call-to-client");
    elBtnMic = $('.web-call-mic-off');
    btnCallOff = $('.web-call-off');
    if (operation == 'showWebCallClientTools') {
        $('#callback-call-voximplant').hide();
        btnCallOff.css("background", "red");
        btnCallOff.css("color", "white");
        btnCallOff.show();
        elBtnMic.show();
        iconWebCall.css("background", "green");
        iconWebCall.css("color", "white");
        iconWebCall.attr("disabled", "true");
    } else if (operation == 'default') {
        $('#callback-call-voximplant').show();
        btnCallOff.hide();
        elBtnMic.hide();
        iconWebCall.css("background", "");
        iconWebCall.css("color", "");
        iconWebCall.removeAttr("disabled");
    } else if (operation == 'callBackToClient') {
        iconCallBackCall.css("background", "green");
        iconCallBackCall.css("color", "white");
        iconCallBackCall.attr("disabled", "true");
    } else if (operation == 'unmuteMicrophone') {
        elBtnMic.css("background", "");
        elBtnMic.css("color", "");
    } else if (operation == 'muteMicrophone') {
        elBtnMic.css("background", "red");
        elBtnMic.css("color", "white");
    }
}