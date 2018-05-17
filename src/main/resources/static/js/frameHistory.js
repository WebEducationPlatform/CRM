$(document).ready(function () {
        $('.open-window-btn').on("click", function openWindow(event) {
            let url = $(this).attr("href");
            window.open(url, "", "width=700,height=500,location=0,menubar=0,titlebar=0");
            return false;
        })
    }
);