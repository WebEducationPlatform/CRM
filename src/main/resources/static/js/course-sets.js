var data = {};
function studentsForCourseSet(courseSetId) {
    let i =courseSetId;
    data = {};

    let urlToGetClientsWithoutPagination = "../rest/client/filtrationForCourseSet";

    //Без city и country ошибка в формировании условия
    data['city'] = '';
    data['country'] = '';
    //Задаем статус из которого выбираем студентов для зачисления в Набор
    data['status'] = 'Учатся';
    //Получаем студентов по статусу
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        dataType: 'json',
        url: urlToGetClientsWithoutPagination,
        data: JSON.stringify(data),
        success: function (res) {
            //Рисуем таблицу
            drawTable(res);
            console.log(JSON.stringify(res));
        },
        error: function (error) {
            console.log(error);
        }
    });

    //Функция отрисовки таблицы
    function drawTable(clients) {
        let table = document.getElementById('students-CourseSet');
        table.style.visibility = '';
        let tableBody = document.getElementById('students-CourseSet-body');

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
        let btn = document.getElementById("StudentsOrEnrollBtn");
        btn.setAttribute("onclick", "checkTableAndEnroll("+courseSetId+")");
    }

}

//Функция зачисления выбранных студентов в Набор
function checkTableAndEnroll(courseSetId) {
    let stdntChckbxs = document.getElementsByName("stdnt");
    let studentsId = [];
    for (let i=0; i<stdntChckbxs.length; i++){
        if(stdntChckbxs[i].checked == true){
            console.log(stdntChckbxs[i].value);
            studentsId.push(stdntChckbxs[i].value);
        }
    }

    $.ajax({
        type: 'POST',
        contentType: "application/json",
        dataType: 'json',
        url: "/rest/courseSet/student/addAll/"+courseSetId,
        data: JSON.stringify(studentsId),
        success: function () {
            console.log('OK');
            alert("Зачислены");
        },
        error: function (error) {
            console.log('ERROR');
            console.log(error);
        }
    });

}