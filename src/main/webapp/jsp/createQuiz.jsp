<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user || sessionScope.user.role != 'admin'}">
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>

<c:set var="pageTitle" value="Create Quiz" scope="request"/>
<%@ include file="/jsp/header.jsp" %>

<div class="container form-container">
    <h2>Create New Quiz</h2>
    <c:if test="${not empty errorMessage}">
        <p class="message error-message"><c:out value="${errorMessage}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/createQuiz" method="post" class="quiz-form" id="createQuizForm">
        <fieldset>
            <legend>Quiz Details</legend>
            <div class="form-group">
                <label for="quizTitle">Quiz Title:</label>
                <input type="text" id="quizTitle" name="quizTitle" required>
            </div>
            <div class="form-group">
                <label for="quizDescription">Description:</label>
                <textarea id="quizDescription" name="quizDescription" rows="3"></textarea>
            </div>
            <div class="form-group">
                <label for="quizCategory">Category:</label>
                <input type="text" id="quizCategory" name="quizCategory" placeholder="e.g., Science, Java">
            </div>
            <div class="form-group">
                <label for="durationMinutes">Duration (minutes):</label>
                <input type="number" id="durationMinutes" name="durationMinutes" min="0" placeholder="0 for no timer">
            </div>
        </fieldset>

        <div id="questionsContainer">
            <%-- Initial Question Block (Index 0) --%>
            <fieldset class="question-block" data-question-index="0">
                <legend>Question 1 (MCQ)</legend>
                <div class="form-group">
                    <label for="questionText_0">Question Text:</label>
                    <textarea id="questionText_0" name="questions[0].questionText" rows="3" required></textarea>
                </div>
                <input type="hidden" name="questions[0].questionType" value="MCQ">

                <div class="options-group">
                    <p>Options (Select the correct answer):</p>
                    <%-- Option 1 --%>
                    <div class="option-item">
                        <input type="radio" id="correctOption_0_0" name="questions[0].correctOptionIndex" value="0" required>
                        <label for="optionText_0_0">Option 1:</label>
                        <input type="text" id="optionText_0_0" name="questions[0].options[0].optionText" required>
                    </div>
                    <%-- Option 2 --%>
                    <div class="option-item">
                        <input type="radio" id="correctOption_0_1" name="questions[0].correctOptionIndex" value="1">
                        <label for="optionText_0_1">Option 2:</label>
                        <input type="text" id="optionText_0_1" name="questions[0].options[1].optionText" required>
                    </div>
                    <%-- Option 3 (Optional) --%>
                    <div class="option-item">
                        <input type="radio" id="correctOption_0_2" name="questions[0].correctOptionIndex" value="2">
                        <label for="optionText_0_2">Option 3:</label>
                        <input type="text" id="optionText_0_2" name="questions[0].options[2].optionText">
                    </div>
                    <%-- Option 4 (Optional) --%>
                    <div class="option-item">
                        <input type="radio" id="correctOption_0_3" name="questions[0].correctOptionIndex" value="3">
                        <label for="optionText_0_3">Option 4:</label>
                        <input type="text" id="optionText_0_3" name="questions[0].options[3].optionText">
                    </div>
                </div>
                <button type="button" class="btn btn-danger btn-sm remove-question-btn" style="margin-top:10px; display:none;">Remove Question</button>
            </fieldset>
        </div>

        <button type="button" id="addQuestionBtn" class="btn btn-secondary" style="margin-top: 15px; margin-bottom:15px;">Add Another Question</button>

        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block">Create Quiz</button>
        </div>
    </form>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const questionsContainer = document.getElementById('questionsContainer');
    const addQuestionBtn = document.getElementById('addQuestionBtn');
    let nextQuestionServerIndex = 1; // Start with 1 as question 0 is hardcoded

    function updateRemoveButtonVisibility() {
        const questionBlocks = questionsContainer.querySelectorAll('.question-block');
        questionBlocks.forEach((block) => {
            const removeBtn = block.querySelector('.remove-question-btn');
            if (removeBtn) {
                removeBtn.style.display = questionBlocks.length > 1 ? 'inline-block' : 'none';
            }
        });
    }

    addQuestionBtn.addEventListener('click', function() {
        const currentQuestionCountInDOM = questionsContainer.querySelectorAll('.question-block').length;
        const displayQuestionNumber = currentQuestionCountInDOM + 1;
        const qIndex = nextQuestionServerIndex;

        // *** DEBUG: Log before generating optionsHTML ***
        console.log("Generating options for new question with qIndex:", qIndex);

        let optionsHTML_Generated = ''; // Use a distinct variable name for clarity
        for (let i = 0; i < 4; i++) {
            const radioReq = (i === 0) ? 'required' : '';
            const textReq = (i < 2) ? 'required' : '';

            // Make sure there are no unescaped backticks or problematic characters inside these strings
            optionsHTML_Generated += '<div class="option-item">';
            optionsHTML_Generated += '<input type="radio" id="correctOption_' + qIndex + '_' + i + '" name="questions[' + qIndex + '].correctOptionIndex" value="' + i + '" ' + radioReq + '>';
            optionsHTML_Generated += '<label for="optionText_' + qIndex + '_' + i + '">Option ' + (i + 1) + ':</label>';
            optionsHTML_Generated += '<input type="text" id="optionText_' + qIndex + '_' + i + '" name="questions[' + qIndex + '].options[' + i + '].optionText" ' + textReq + '>';
            optionsHTML_Generated += '</div>';
        }

        // *** DEBUG: Log the generated optionsHTML ***
        console.log("Generated optionsHTML:", optionsHTML_Generated);

        // Ensure no backticks are within this main template literal where they could cause issues
        // if some JS variable was unexpectedly a string containing a backtick.
        // Using simple string concatenation for the main block too for maximum safety here.
        let newQuestionBlockHTML_Generated = '';
        newQuestionBlockHTML_Generated += '<fieldset class="question-block" data-question-index="' + qIndex + '">';
        newQuestionBlockHTML_Generated += '<legend>Question ' + displayQuestionNumber + ' (MCQ)</legend>';
        newQuestionBlockHTML_Generated += '<input type="hidden" name="questions[' + qIndex + '].questionType" value="MCQ">';
        newQuestionBlockHTML_Generated += '<div class="form-group">';
        newQuestionBlockHTML_Generated += '<label for="questionText_' + qIndex + '">Question Text:</label>';
        newQuestionBlockHTML_Generated += '<textarea id="questionText_' + qIndex + '" name="questions[' + qIndex + '].questionText" rows="3" required></textarea>';
        newQuestionBlockHTML_Generated += '</div>';
        newQuestionBlockHTML_Generated += '<div class="options-group">';
        newQuestionBlockHTML_Generated += '<p>Options (Select the correct answer):</p>';
        newQuestionBlockHTML_Generated += optionsHTML_Generated; // Inject the options here
        newQuestionBlockHTML_Generated += '</div>';
        newQuestionBlockHTML_Generated += '<button type="button" class="btn btn-danger btn-sm remove-question-btn" style="margin-top:10px;">Remove Question</button>';
        newQuestionBlockHTML_Generated += '</fieldset>';

        // *** DEBUG: Log the full newQuestionBlockHTML ***
        console.log("Full newQuestionBlockHTML:", newQuestionBlockHTML_Generated);

        questionsContainer.insertAdjacentHTML('beforeend', newQuestionBlockHTML_Generated);
        nextQuestionServerIndex++;
        updateRemoveButtonVisibility();
    });

    questionsContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('remove-question-btn')) {
            const questionBlocks = questionsContainer.querySelectorAll('.question-block');
            const blockToRemove = event.target.closest('.question-block');

            if (questionBlocks.length === 1 && blockToRemove === questionBlocks[0]) {
                 return;
            }
            blockToRemove.remove();

            const remainingBlocks = questionsContainer.querySelectorAll('.question-block');
            remainingBlocks.forEach((block, domIndex) => {
                block.querySelector('legend').textContent = `Question ${domIndex + 1} (MCQ)`;
            });
            updateRemoveButtonVisibility();
        }
    });
    updateRemoveButtonVisibility();
});
</script>

<%@ include file="/jsp/footer.jsp" %>