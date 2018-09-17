$('#oncall').hide();
$('#oncall_msg').hide();
$('#unhold').attr('disabled','disabled');
$('#hold').removeAttr( "disabled" );
$('#unmute').attr('disabled','disabled');
$('#mute').removeAttr( "disabled" );
var target, msg_target;
$('#saveSettings').on('click',function(){
        $('#status').html('');
	display_name = $('#yourname').val();
	sip_uri = $('#authname').val()+'@'+$('#sipuri').val();
	sip_password = $('#password').val();
	ws_servers = $('#wssuri').val();
	var aliceSimple = createSimple(sip_uri, display_name, 'remoteVideo', 'dial_num', sip_password, ws_servers);
        
        aliceSimple.on('registered', function(){
                console.info('[SIP Phone] : Connected (Registered)');
                $('#status').html('Connected (Registered)');
        });
        aliceSimple.on('registrationFailed', function(){
                console.info('[SIP Phone] : Failed (Unregistered)');
                $('#status').html('Failed (Unregistered)');
        });
        $('#myModal').modal('hide');
});

function createSimple(callerURI, displayName, remoteVideo, buttonId, sip_password, ws_servers) {
    var remoteVideoElement = document.getElementById(remoteVideo);
    var button = document.getElementById(buttonId);
    var answer_button = document.getElementById('answerCall');
    var reject_button = document.getElementById('rejectCall');
    var hold_button = document.getElementById('hold');
    var unhold_button = document.getElementById('unhold');
    var mute_button = document.getElementById('mute');
    var unmute_button = document.getElementById('unmute');
    var dtmf_button = document.getElementById('send_dtmf');

    var configuration = {
        media: {
            remote: {
//                 video: remoteVideoElement,
                audio: remoteVideoElement
            }
        },
        ua: {
            uri: callerURI,
            password: sip_password,
            wsServers: ws_servers,
            displayName: displayName,
            authorizationUser: null,
            register: true,
            rel100:SIP.C.supported.SUPPORTED,
            registerExpires: null,
            noAnswerTimeout: null,
            traceSip: true,
            stunServers: null,
            turnServers: null,
            usePreloadedRoute: null,
            connectionRecoveryMinInterval: null,
            connectionRecoveryMaxInterval: null,
            hackViaTcp: null,
            hackIpInContact: null,
            userAgentString: 'SIP Js',
        }
    };
    var simple = new SIP.WebRTC.Simple(configuration);

    simple.on('ended', function() {
        remoteVideoElement.style.visibility = 'hidden';
        button.firstChild.nodeValue = 'Dial';
        $('#oncall').hide();
        $('#oncall_msg').hide();
        $('#number').show();
        $('#messages').html('');
        $('#number').val('');
        $('#unhold').attr('disabled','disabled');
        $('#hold').removeAttr( "disabled" );
        $('#unmute').attr('disabled','disabled');
        $('#mute').removeAttr( "disabled" );
    });

    simple.on('connected', function() {
        console.info('[SIP Phone] : Connected (On Call)');
        $('#oncall').show();
        $('#oncall_msg').show();
        remoteVideoElement.style.visibility = 'visible';
        button.firstChild.nodeValue = 'Hang up';
        $('#number').hide();
    });

    simple.on('ringing', function() {
        $('#incomingCallModal').modal('show');
        console.log('Incoming call ', simple);
    });
    
    answer_button.addEventListener('click', function() {
        var session = simple.answer();
        msg_target = session.remoteIdentity.uri.user;
        $('#incomingCallModal').modal('hide');
    });
    
    reject_button.addEventListener('click', function() {
        simple.reject();
        $('#incomingCallModal').modal('hide');
    });
    
    dtmf_button.addEventListener('click', function() {
        var dtmf_tone = $('#dtmf').val();
        if(dtmf_tone !== ''){
            simple.sendDTMF(dtmf_tone);
        }
    });

    button.addEventListener('click', function() {
        if (simple.state === SIP.WebRTC.Simple.C.STATUS_NULL ||
            simple.state === SIP.WebRTC.Simple.C.STATUS_COMPLETED) {
            target = $('#number').val()+'@'+$('#sipuri').val();
            msg_target = $('#number').val();
            if(target !== ''){
                $('#number').hide();
                button.firstChild.nodeValue = 'Calling to '+$('#number').val();
                simple.call(target);
            }
            
        } else {
            simple.hangup();
        }
    });
    
    hold_button.addEventListener('click', function() {
        simple.hold();
        $('#hold').attr('disabled','disabled');
        $('#unhold').removeAttr( "disabled" );
    });
    
    unhold_button.addEventListener('click', function() {
        simple.unhold();
        $('#unhold').attr('disabled','disabled');
        $('#hold').removeAttr( "disabled" );
    });
    
    mute_button.addEventListener('click', function() {
        simple.mute();
        $('#mute').attr('disabled','disabled');
        $('#unmute').removeAttr( "disabled" );
    });
    
    unmute_button.addEventListener('click', function() {
        simple.unmute();
        $('#unmute').attr('disabled','disabled');
        $('#mute').removeAttr( "disabled" );
    });
    
    var messageRender = document.getElementById("messages");
    var messageInput = document.getElementById("msg_text");

    function sendMessage() {
        var msg = messageInput.value;
        
        if (msg !== '' && msg_target !== '') {
            messageInput.value = '';
            simple.message(msg_target+'@'+$('#sipuri').val(), msg);
            var msgTag = createMsgTag($('#yourname').val(), msg);
            messageRender.appendChild(msgTag);
            $("#messages").animate({ scrollTop: $('#messages').prop("scrollHeight")}, 1000);
        }
    }
    simple.on('message', function (msg) {
        console.info('Incoming chat msg', msg);
        if(msg.remoteIdentity.displayName === ''){
                var remote_user = target;
        } else {
                var remote_user = msg.remoteIdentity.displayName;
        }
        if(msg.contentType=="message/imdn+xml") {
                // Ignore
        } else {
                var msgTag = createMsgTag(remote_user, msg.body);
                messageRender.appendChild(msgTag);
                $("#messages").animate({ scrollTop: $('#messages').prop("scrollHeight")}, 1000);
        }
    });

    messageInput.onkeydown = (function(e) {
        if(e.keyCode == 13 && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
    
    return simple;
}

function createMsgTag(from, msgBody) {
    var msgTag = document.createElement('p');
    msgTag.className = 'message';
    
    var fromTag = document.createElement('span');
    fromTag.className = 'message-from';
    fromTag.appendChild(document.createTextNode(from + ':'));
    var msgBodyTag = document.createElement('span');
    msgBodyTag.className = 'message-body';
    msgBodyTag.appendChild(document.createTextNode(' ' + msgBody));
    msgTag.appendChild(fromTag);
    msgTag.appendChild(msgBodyTag);
    return msgTag;
}

(function () {
if (window.RTCPeerConnection) {

}
})();
