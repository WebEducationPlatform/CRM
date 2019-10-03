var data = {};
var crsStId;

//Функция вывода списка статусов для выбора условия фильтрации студентов
function selectStudentsForCourseSet(courseSetId) {
    crsStId = courseSetId;
    //Выводим строку выбора
    let selector = document.getElementById("statuses");
    selector.style.visibility="";
    //Выводим варианты статусов
    $.ajax({
        url: '/rest/status/dto/for-mailing',
        type: 'GET',
        async: true,
        success: function (data) {
            for (let i = 0; i < data.length; i++) {
                let option = document.createElement("option");
                option.innerText = data[i].name;
                option.setAttribute("value", data[i].name);
                selector.appendChild(option);
            }
        }
    });
}

//Когда в списке. который создан выше выбирается статус...
function changeFunc() {
    let selectBox = document.getElementById("statuses");
    let selectedValue = selectBox.options[selectBox.selectedIndex].value;
    //Запускается функция получения Студентов и отрисовки таблицы
    studentsForCourseSet(selectedValue);
}

//функция получения Студентов и отрисовки таблицы
function studentsForCourseSet(fltrCondition) {
    document.getElementById("divTableStdntCrsSt").innerText = " ";
    document.getElementById("EnrollBtn").style.visibility="";

    data = {};

    let urlToGetClientsWithoutPagination = "../rest/client/filtrationForCourseSet";

    //Без city и country ошибка в формировании условия
    data['city'] = '';
    data['country'] = '';
    //Задаем статус из которого выбираем студентов для зачисления в Набор
    data['status'] = fltrCondition;
    //Получаем студентов по статусу
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        dataType: 'json',
        url: urlToGetClientsWithoutPagination,
        data: JSON.stringify(data),
        beforeSend: function(){
            document.getElementById("divTableStdntCrsSt").innerText = "Загружаю...";
        },
        success: function (res) {
            document.getElementById("divTableStdntCrsSt").innerText = " ";
            //Рисуем таблицу
            drawTable(res, crsStId);
        },
        error: function (error) {
            console.log(error);
        },
    });
}

//Функция отрисовки таблицы
function drawTable(clients, courseSetId) {
    let table = document.getElementById('students-CourseSet');
    table.style.visibility = '';
    let tableBody = document.getElementById('students-CourseSet-body');

    tableBody.innerHTML = "";

    for (let i=0; i<clients.length; i++) {
        // table row
        let row = document.createElement("tr");

        for (let j = 0; j < 5; j++) {
            let cell = document.createElement("td");
            if (j==0){cell.innerText = clients[i].id;}
            if (j==1){cell.innerText = clients[i].name;}
            if (j==2){cell.innerText = clients[i].lastName}
            if (j==3){cell.innerText = clients[i].city}
            if (j==4){cell.innerHTML = '<input type="checkbox" name="stdnt" value="'+clients[i].id+'">'}

            row.appendChild(cell);
        }
        tableBody.appendChild(row);
    }
    table.appendChild(tableBody);
    let btn = document.getElementById("EnrollBtn");
    btn.setAttribute("onclick", "checkTableAndEnroll("+courseSetId+")");
}

//Функция зачисления выбранных студентов в Набор
function checkTableAndEnroll(courseSetId) {
    let stdntChckbxs = document.getElementsByName("stdnt");
    let studentsId = [];
    for (let i=0; i<stdntChckbxs.length; i++){
        if(stdntChckbxs[i].checked == true){
             studentsId.push(stdntChckbxs[i].value);
        }
    }

    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: "/rest/courseSet/student/addAll/"+courseSetId,
        data: JSON.stringify(studentsId),
        success: function () {
            document.getElementById('students-CourseSet').style.visibility="hidden";
            document.getElementById("divTableStdntCrsSt").innerText = "Студенты зачислены";
            document.getElementById("EnrollBtn").style.visibility="hidden";
            document.getElementById("statuses").style.visibility="hidden";
        },
        error: function (error) {
             console.log(error);
        }
    });

}

