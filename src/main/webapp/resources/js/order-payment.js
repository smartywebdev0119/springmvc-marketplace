function toggleDebitCartPaymentMethod() {

    var element = document.getElementById("collapsible-id");

    if (element.getAttribute("class") === "collapsible") {

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


function validateDebitCard() {

    var debitCardNumber = document.debit_credit_card_name.debit_card_number.value;
    var debitCardCvv = document.debit_credit_card_name.debit_card_cvv.value;

    var errorDiv = document.getElementById("debit-card-error-div");

    errorDiv.textContent = "";

    if (debitCardNumber.length === 16
        && isNumeric(debitCardNumber)) {

    } else {
        errorDiv.textContent = "card number is not valid";
        return false;
    }

    if ((debitCardCvv.length === 3 ||
        debitCardCvv.length === 4)
        && isNumeric(debitCardCvv)) {

    } else {
        errorDiv.textContent = "card cvv is not valid";
        return false;
    }

    return true;
}


function isNumeric(num) {
    return !isNaN(num)
}