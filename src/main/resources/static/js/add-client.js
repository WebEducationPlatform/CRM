function addClient() {
	if($("#saveChanges")[0].className ==="btn btn-primary disabled"){
		return;
	}
	var SN = [];
	var $th = $('#SocialNetworks').find('th');
	try {
		$('#SocialNetworks').find('tbody tr').each(function (i, tr) {
			var obj = {}, $tds = $(tr).find('td');
			$th.each(function (index, th) {
				if ($(th)[0].innerText !== "id" && $tds.eq(index).text() === "") {
					var current = document.getElementById("message");
					current.textContent = "Заполните пустые поля в таблице 'Cоциальные сети'";
					current.style.color = "red";
					throw new Error("Пустые поля в таблице 'Cоциальные сети'");
				}
				if ($(th).attr('abbr') !== "") {
					if(typeof $tds.eq(index).children().val()==='undefined') {
						var obj1 = {};
						if($tds.eq(index)[0].id === "edit-client-SN_type"){
							obj1["name"] = $tds.eq(index)[0].innerText;
							obj[$(th).attr('abbr')] = obj1;
						}else {
							obj[$(th).attr('abbr')] = $tds.eq(index).text();
						}
					}else{
						obj[$(th).attr('abbr')] = $tds.eq(index).children().val();
					}
				}
			});
			SN.push(obj);
		});
	} catch (e) {
		return;
	}
	var Job = [];
	$th = $('#Job').find('th');
	try {
		$('#Job').find('tbody tr').each(function (i, tr) {
			var obj = {}, $tds = $(tr).find('td');
			$th.each(function (index, th) {
				if ($(th)[0].innerText !== "id" && $tds.eq(index).text() === "") {
					var current = document.getElementById("message");
					current.textContent = "Заполните пустые поля в таблице 'Работа'";
					current.style.color = "red";
					throw new Error("Пустые поля в таблице 'Работа'")
				}
				if ($(th).attr('abbr') !== "") {
					obj[$(th).attr('abbr')] = $tds.eq(index).text();
				}
			});
			Job.push(obj);
		});
	} catch (e) {
		return;
	}

	let status = {
	    name : $('#client-status').val(),
    };

    let url = '/admin/rest/client/add';
	let wrap = {
		name: $('#edit-client-first-name').val(),
		lastName: $('#edit-client-last-name').val(),
		phoneNumber: $('#edit-client-phone-number').val(),
		email: $('#edit-client-email').val(),
		age: $('#edit-client-age').val(),
		sex: $('#edit-client-sex').find('option:selected').text(),
		state:  $('#edit-client-state').val(),
		country: $('#edit-client-country').val(),
		city: $('#edit-client-city').val(),
		skype: $('#edit-client-skype').val(),
		status: status,
		socialNetworks: SN,
		jobs: Job
	};
	let data = JSON.stringify(wrap);
	var current = document.getElementById("message");
	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json; charset=utf-8",
		data: data,
		beforeSend: function(){
			current.style.color = "darkorange";
			current.textContent = "Загрузка...";

		},
		success: function () {
			current.textContent = "Сохранено";
			current.style.color = "limegreen";
			location.reload();
		},
		error: function (e) {
			console.log(e.responseText);
			current.textContent = "Ошибка сохранения. " + e.responseText;
			current.style.color = "red";
		}
	});
}

function disableInputE() {
	var disMas = [69, 187, 189, 109];
	if (disMas.indexOf(event.keyCode)!==-1) {
		event.preventDefault()
	}
}

$(document).on('click', 'td', (function (e) {
	if (e.target.localName !== "td" || e.target.firstElementChild !== null || (e.target.offsetParent.id !== "SocialNetworks" && e.target.offsetParent.id !== "Job") || $('#edit-client-first-name')[0].disabled) {
		return;
	}
	var t = e.target || e.srcElement;
	var elm_name = t.tagName.toLowerCase();
	if (elm_name === 'input') {
		return false;
	}
	var val = $(t).html();
	var code;
	if(e.target.cellIndex === 2 && e.target.offsetParent.id === "SocialNetworks"){
		code = '<select id="edit" value = "'+ val + '" class=\"form-control\">' + SNs + '</select>'
	}else {
		code = '<input type="text" id="edit" value="' + val + '" />';
	}
	$(t).empty().append(code);
	var newEditElement = $("#edit");
	selectOptions(newEditElement);
	newEditElement.focus();
	newEditElement.blur(function () {
		var val = $(this).val();
		$(this).parent().empty().html(val);
	});
}));

$(window).keydown(function (event) {
	if (event.target.id === "edit") {
		if (event.keyCode === 13) {
			$('#edit').blur();
		}
	}
});

function deleteSocial(element) {
	$(element).parent().parent().remove();
}

function deleteJob(element) {
	$(element).parent().parent().remove();
}

var SNs="";
function addNewSN() {
	if(SNs.length===0){
		console.log("Массив SocialNetworkTypes пуст!");
		return;
	}
	var size =  ($("#SN-table-body")[0]).rows.length;
	$("#SN-table-body").append("<tr><td hidden=\"hidden\"></td><td></td><td></td><td><button type=\"button\" onclick=\"deleteSocial(this)\" class=\"glyphicon glyphicon-remove\"></button></td></tr>")
}

function addNewJob() {
	var size =  ($("#job-table-body")[0]).rows.length;
	$("#job-table-body").append("<tr><td hidden=\"hidden\"></td><td></td><td></td><td><button type=\"button\" onclick=\"deleteJob(this)\" class=\"glyphicon glyphicon-remove\"></button></td></tr>")
}

$(function () {
	$('#client-from').validator({
		disable: true
	})
});

var socialNetworkTypes = [];

$(document).ready(function () {

	var url = '/user/socialNetworkTypes';
	$.ajax({
		type: 'get',
		url: url,
		dataType: 'json',
		success: function (res) {
			socialNetworkTypes=res;
			$.each(socialNetworkTypes,function (index, type) {
				SNs = SNs + "<option>"+ type + "</option>"
			});
		},
		error: function (error) {
			console.log(error);
		}
	});
});

function selectOptions (element) {
	element.find("option").each(function () {
		var val = $(this).closest('select').attr('value');
		var inText = $(this).text();
		if (val === inText) {
			$(this).attr("selected", "selected");
		}
	});
}
$(function () {
	$("#edit-client-age").on('keyup', function(e) {
		var reg = new RegExp("^$|^[0-9]$|^[1-9][0-9]$|^1[0-1][1-9]$|^12[1-7]$");
		if(!reg.test($("#edit-client-age").val())) {
			$("#edit-client-age").siblings("div[class='help-block with-error']")[0].innerText = "Диапазон от 0 до 127";
			$("#saveChanges")[0].setAttribute("disabled","disabled");
		}else {
			$("#edit-client-age").siblings("div[class='help-block with-error']")[0].innerText = "";
			$("#saveChanges")[0].removeAttribute("disabled");
		}
	});
});
