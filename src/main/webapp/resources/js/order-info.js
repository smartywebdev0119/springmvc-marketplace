function updateBullet(status,id) {

    var json = {
        1: "not-yet-bullet",
        2: "passed-with-success-bullet",
        3: "fail-bullet",
        4: "in-progress-bullet"
    };

    return json[status]
}