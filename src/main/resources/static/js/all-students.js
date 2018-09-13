$('.checkbox').click(function() {
    var table, rows, i, status;
    table = document.getElementById("students-table");
    rows = table.rows;
    for (i = 1; i < rows.length; i++) {
        status = rows[i].getElementsByTagName("TD")[0];
        if (this.id == status.innerHTML) {
            rows[i].style.display = this.checked ? '' : 'none';
        }
    }
});

function sort_table(n, type) {
    var table, rows, switching, i, x, y, x_val, y_val, temp_x, temp_y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("students-table");
    switching = true;
    dir = "asc";
    while (switching) {
        switching = false;
        rows = table.rows;
        for (i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            x = rows[i].getElementsByTagName("TD")[n];
            y = rows[i + 1].getElementsByTagName("TD")[n];
            if(type == "href") {
                x_val = x.innerText.toLowerCase();
                y_val = y.innerText.toLowerCase();
            } else if(type == "date") {
                temp_x = x.innerHTML.toLowerCase().split(".");
                temp_y = y.innerHTML.toLowerCase().split(".");
                x_val = new Date(temp_x[2], temp_x[1] - 1, temp_x[0]);
                y_val = new Date(temp_y[2], temp_y[1] - 1, temp_y[0]);
            } else {
                x_val = x.innerHTML.toLowerCase();
                y_val = y.innerHTML.toLowerCase();
            }
            if (dir == "asc") {
                if (x_val > y_val) {
                    shouldSwitch= true;
                    break;
                }
            } else if (dir == "desc") {
                if (x_val < y_val) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            switchcount ++;
        } else {
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

$('.button_edit').click(function () {
    alert("TODO Show modal");
    console.log(this.value);
});
