<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user || sessionScope.user.role != 'admin'}">
    <c:redirect url="/login"/>
</c:if>
<c:if test="${empty quizToEdit}">
    <c:redirect url="${pageContext.request.contextPath}/viewQuizzes?errorMessage=QuizDataNotAvailableForEdit"/>
</c:if>

<c:set var="pageTitle" value="Edit Quiz: ${quizToEdit.title}" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container form-container">
    <h2>Edit Quiz: <c:out value="${quizToEdit.title}"/></h2>
    <c:if test="${not empty errorMessage}">
        <p class="error-message">${errorMessage}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/editQuiz" method="post" class="quiz-form" id="editQuizForm">
        <input type="hidden" name="quizId" value="${quizToEdit.id}">
        <fieldset>
            <legend>Quiz Details</legend>
            <div class="form-group">
                <label for="quizTitle">Quiz Title:</label>
                <input type="text" id="quizTitle" name="quizTitle" value="<c:out value="${quizToEdit.title}"/>" required>
            </div>
            <div class="form-group">
                <label for="quizDescription">Description:</label>
                <textarea id="quizDescription" name="quizDescription" rows="3"><c:out value="${quizToEdit.description}"/></textarea>
            </div>
            <div class="form-group">
                <label for="quizCategory">Category:</label>
                <input type="text" id="quizCategory" name="quizCategory" value="<c:out value="${quizToEdit.category}"/>" placeholder="e.g., Science, Java">
            </div>
            <div class="form-group">
                <label for="durationMinutes">Duration (minutes):</label>
                <input type="number" id="durationMinutes" name="durationMinutes" value="${quizToEdit.durationMinutes > 0 ? quizToEdit.durationMinutes : ''}" min="0" placeholder="0 for no timer">
            </div>
        </fieldset>

        <div id="questionsContainer">
            <c:forEach var="question" items="${quizToEdit.questions}" varStatus="qStatus">
                <fieldset class="question-block" data-question-index="${qStatus.index}">
                    <legend>Question ${qStatus.count} (MCQ)</legend>
                    <input type="hidden" name="questions[${qStatus.index}].questionId" value="${question.id}">
                    <input type="hidden" name="questions[${qStatus.index}].questionType" value="MCQ">

                    <div class="form-group">
                        <label for="questionText_${qStatus.index}">Question Text:</label>
                        <textarea id="questionText_${qStatus.index}" name="questions[${qStatus.index}].questionText" rows="3" required><c:out value="${question.questionText}"/></textarea>
                    </div>

                    <div class="options-group">
                        <p>Options (Select the correct answer):</p>
                        <c:set var="optionsCount" value="0"/>
                        <c:forEach var="option" items="${question.options}" varStatus="oStatus">
                            <div class="option-item">
                                <input type="radio" id="correctOption_${qStatus.index}_${oStatus.index}"
                                       name="questions[${qStatus.index}].correctOptionIndex"
                                       value="${oStatus.index}" ${option.correct ? 'checked' : ''} required>
                                <label for="optionText_${qStatus.index}_${oStatus.index}">Option ${oStatus.count}:</label>
                                <input type="text" id="optionText_${qStatus.index}_${oStatus.index}"
                                       name="questions[${qStatus.index}].options[${oStatus.index}].optionText"
                                       value="<c:out value="${option.optionText}"/>" required>
                            </div>
                            <c:set var="optionsCount" value="${optionsCount + 1}"/>
                        </c:forEach>
                        <%-- Fill remaining potential option slots if less than 4 options exist --%>
                        <c:if test="${optionsCount < 4}">
                            <c:forEach begin="${optionsCount}" end="3" var="fillIndex"> <%-- Iterate based on index, not varStatus.index --%>
                                 <div class="option-item">
                                    <input type="radio" id="correctOption_${qStatus.index}_${fillIndex}"
                                           name="questions[${qStatus.index}].correctOptionIndex"
                                           value="${fillIndex}" required> <%-- Cannot pre-check empty options --%>
                                    <label for="optionText_${qStatus.index}_${fillIndex}">Option ${fillIndex + 1}:</label>
                                    <input type="text" id="optionText_${qStatus.index}_${fillIndex}"
                                           name="questions[${qStatus.index}].options[${fillIndex}].optionText">
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                    <button type="button" class="btn btn-danger btn-sm remove-question-btn" style="margin-top:10px;">Remove This Question</button>
                </fieldset>
            </c:forEach>
        </div>

        <button type="button" id="addQuestionBtn" class="btn btn-secondary" style="margin-top: 15px; margin-bottom:15px;">Add New Question</button>

        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block">Save Changes</button>
        </div>
    </form>
</div>
<script>
document.addEventListener('DOMContentLoaded', function() {
    const questionsContainer = document.getElementById('questionsContainer');
    const addQuestionBtn = document.getElementById('addQuestionBtn');
    let nextQuestionServerIndex = questionsContainer.querySelectorAll('.question-block').length;

    function updateRemoveButtonVisibility() {
        // ... (same as before)
        const questionBlocks = questionsContainer.querySelectorAll('.question-block');
        questionBlocks.forEach((block) => {
            const removeBtn = block.querySelector('.remove-question-btn');
            if (removeBtn) {
                removeBtn.style.display = 'inline-block';
            }
        });
    }

    addQuestionBtn.addEventListener('click', function() {
        const currentQuestionCountInDOM = questionsContainer.querySelectorAll('.question-block').length;
        const displayQuestionNumber = currentQuestionCountInDOM + 1;
        const qIndex = nextQuestionServerIndex;

        console.log("EDIT_QUIZ: Generating options for new question with qIndex:", qIndex);

        let optionsHTML_Generated = '';
        for (let i = 0; i < 4; i++) {
            const radioReq = (i === 0) ? 'required' : '';
            const textReq = (i < 2) ? 'required' : '';

            optionsHTML_Generated += '<div class="option-item">';
            optionsHTML_Generated += '<input type="radio" id="new_correctOption_' + qIndex + '_' + i + '" name="questions[' + qIndex + '].correctOptionIndex" value="' + i + '" ' + radioReq + '>';
            optionsHTML_Generated += '<label for="new_optionText_' + qIndex + '_' + i + '">Option ' + (i + 1) + ':</label>';
            optionsHTML_Generated += '<input type="text" id="new_optionText_' + qIndex + '_' + i + '" name="questions[' + qIndex + '].options[' + i + '].optionText" ' + textReq + '>';
            optionsHTML_Generated += '</div>';
        }

        console.log("EDIT_QUIZ: Generated optionsHTML:", optionsHTML_Generated);

        let newQuestionBlockHTML_Generated = '';
        newQuestionBlockHTML_Generated += '<fieldset class="question-block" data-question-index="' + qIndex + '">';
        newQuestionBlockHTML_Generated += '<legend>Question ' + displayQuestionNumber + ' (MCQ) - New</legend>';
        newQuestionBlockHTML_Generated += '<input type="hidden" name="questions[' + qIndex + '].questionId" value="NEW">';
        newQuestionBlockHTML_Generated += '<input type="hidden" name="questions[' + qIndex + '].questionType" value="MCQ">';
        newQuestionBlockHTML_Generated += '<div class="form-group">';
        newQuestionBlockHTML_Generated += '<label for="new_questionText_' + qIndex + '">Question Text:</label>';
        newQuestionBlockHTML_Generated += '<textarea id="new_questionText_' + qIndex + '" name="questions[' + qIndex + '].questionText" rows="3" required></textarea>';
        newQuestionBlockHTML_Generated += '</div>';
        newQuestionBlockHTML_Generated += '<div class="options-group">';
        newQuestionBlockHTML_Generated += '<p>Options (Select the correct answer):</p>';
        newQuestionBlockHTML_Generated += optionsHTML_Generated; // Inject the options here
        newQuestionBlockHTML_Generated += '</div>';
        newQuestionBlockHTML_Generated += '<button type="button" class="btn btn-danger btn-sm remove-question-btn" style="margin-top:10px;">Remove This Question</button>';
        newQuestionBlockHTML_Generated += '</fieldset>';

        console.log("EDIT_QUIZ: Full newQuestionBlockHTML:", newQuestionBlockHTML_Generated);

        questionsContainer.insertAdjacentHTML('beforeend', newQuestionBlockHTML_Generated);
        nextQuestionServerIndex++;
        updateRemoveButtonVisibility();
    });

    questionsContainer.addEventListener('click', function(event) {
        // ... (remove logic same as before) ...
        if (event.target.classList.contains('remove-question-btn')) {
            event.target.closest('.question-block').remove();

            const remainingBlocks = questionsContainer.querySelectorAll('.question-block');
            remainingBlocks.forEach((block, index) => {
                const legend = block.querySelector('legend');
                const idInput = block.querySelector('input[name*="questionId"]');
                let legendSuffix = "";
                if (idInput && idInput.value === 'NEW') {
                    legendSuffix = " - New";
                }
                legend.textContent = `Question ${index + 1} (MCQ)${legendSuffix}`;
            });
            updateRemoveButtonVisibility();
        }
    });
    updateRemoveButtonVisibility();
});
</script>

<%@ include file="footer.jsp" %>