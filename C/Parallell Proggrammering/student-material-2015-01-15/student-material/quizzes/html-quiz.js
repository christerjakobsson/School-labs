window.onload = function() {
    document.getElementById("submit").onclick = function() {
        var quiz = document.quiz;
        var numquestions = quiz.numquestions.value;
        var numok = 0;
        for (var question = 1; question <= numquestions; ++question) {
            var answer = false;
            var radios = document.getElementsByName("q" + question);
            for (var i = 0; i < radios.length; ++i) {
                if (radios[i].checked) {
                    answer = radios[i].value;
                    break;
                }
            }
            var realanswer = document.getElementById("q" + question + "a");
            if (realanswer != null) {
                realanswer = realanswer.value;
                if (realanswer == answer) {
                    numok++;
                }
            }
        }
        quiz.grade.value = "" + numok + " of " + numquestions;
    }
}
