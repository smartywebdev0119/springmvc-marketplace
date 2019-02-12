function toggleDebitCartPaymentMethod() {

    var element = document.getElementById("collapsible-id");

    if (element.getAttribute("class") === "collapsible"){

        element.setAttribute("class", "collapsible active");
        document
            .getElementById("content-id")
            .setAttribute("style", "display: block;")
        console.log("block");

    } else {
        console.log("none");

        element.setAttribute("class", "collapsible");
        document
            .getElementById("content-id")
            .setAttribute("style", "display: none;")
    }


    // for multiple collapsible blocks ?
    // https://www.w3schools.com/howto/howto_js_collapsible.asp
    // var coll = document.getElementById("collapsible-id");
    // var i;
    //
    // for (i = 0; i < coll.length; i++) {
    // coll.addEventListener("click", function () {
    //
    //     console.log("button clicked");
    //
    //     this.classList.toggle("active");
    //
    //     var content = this.nextElementSibling;
    //     if (content.style.display === "block") {
    //
    //         console.log("none");
    //         content.style.display = "none";
    //     } else {
    //
    //         console.log("block");
    //         content.style.display = "block";
    //     }
    // });
    // }

}