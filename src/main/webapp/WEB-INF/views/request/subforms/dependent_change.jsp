<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>

<div class="request-group">
    <label>Proposer</label>
    <input type="text" class="request-input" value="${sessionScope.currentUser.fullName}" readonly />
</div>

<div class="request-group">
    <label>Proposal Date</label>
    <input type="text" class="request-input" value="${now}" readonly />
</div>

<div class="request-group">
    <label>Approver <span class="required-star">*</span></label>
    <select name="approverId" class="request-select" required>
        <option value="" disabled selected>-- Select Payroll Staff --</option>
        <c:forEach items="${payrollStaffList}" var="pr">
            <option value="${pr.id}">${pr.fullName} - ${pr.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <label for="changeType">Change Type <span class="required-star">*</span></label>
    <select name="changeType" id="changeType" class="request-select" required onchange="toggleDependentFields()">
        <option value="ADD" selected>Add Dependent</option>
        <option value="UPDATE">Update Dependent</option>
        <option value="REMOVE">Remove Dependent</option>
    </select>
</div>

<div class="request-group" id="dependentSelectorGroup" style="display: none;">
    <label for="dependentId">Select Dependent <span class="required-star">*</span></label>
    <select name="dependentId" id="dependentId" class="request-select">
        <option value="" disabled selected>-- Select Dependent --</option>
        <c:forEach items="${activeDependents}" var="dep">
            <option value="${dep.id}" data-name="${dep.dependentName}" data-dob="${dep.dependentDob}" data-idnumber="${dep.dependentIdNumber}" data-relationship="${dep.relationship}">${dep.dependentName} (${dep.relationship})</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <label for="dependentStatus">Target Status</label>
    <input type="text" id="dependentStatus" class="request-input" value="Active" readonly style="background-color: #f8f9fa; font-weight: bold;" />
</div>

<div class="request-group">
    <label for="effectiveDate">Target Effective Date</label>
    <input type="text" id="effectiveDate" class="request-input" value="${today}" readonly style="background-color: #f8f9fa;" />
</div>

<div class="request-group dependent-detail-field">
    <label for="dependentName">Dependent Full Name <span class="required-star">*</span></label>
    <input type="text" name="dependentName" id="dependentName" class="request-input" required placeholder="Enter dependent's full name" />
</div>

<div class="request-group dependent-detail-field">
    <label for="dependentDob">Dependent Date of Birth <span class="required-star">*</span></label>
    <input type="date" name="dependentDob" id="dependentDob" class="request-input" required />
</div>

<div class="request-group dependent-detail-field">
    <label for="dependentIdNumber">Personal Identification Number <span class="required-star">*</span></label>
    <input type="text" name="dependentIdNumber" id="dependentIdNumber" class="request-input" required placeholder="Enter 12-digit Citizen ID" pattern="\d{12}" maxlength="12" minlength="12" title="Personal ID number must be exactly 12 digits" oninput="this.value = this.value.replace(/[^0-9]/g, '')" />
</div>

<div class="request-group dependent-detail-field">
    <label for="relationship">Relationship with Proposer <span class="required-star">*</span></label>
    <input type="text" name="relationship" id="relationship" class="request-input" required placeholder="e.g., Child, Spouse, Parent" />
</div>

<div class="request-group dependent-detail-field">
    <label for="dependentFile">Proof Image <span class="required-star">*</span></label>
    <input type="file" name="dependentFile" id="dependentFile" class="request-input" accept="image/*" required />
</div>

<div class="request-group">
    <label>Observer(s)</label>
    <select class="select2-dynamic request-select" name="observerIds" multiple="multiple" style="width:100%">
        <c:forEach items="${observerList}" var="obs">
            <option value="${obs.id}">${obs.fullName} - ${obs.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group" style="grid-column: 1 / -1;">
    <label for="reasonEditor">Reason <span class="required-star">*</span></label>
    <textarea name="reason" id="reasonEditor" class="request-textarea" rows="5" required></textarea>
</div>

<script>
    // Set maximum allowed DOB to today
    document.getElementById('dependentDob').max = new Date().toISOString().split('T')[0];

    // Populate detail inputs when selecting an existing dependent for UPDATE
    document.getElementById('dependentId').addEventListener('change', function() {
        var changeType = document.getElementById('changeType').value;
        if (changeType === 'UPDATE') {
            var selectedOption = this.options[this.selectedIndex];
            if (selectedOption && selectedOption.value) {
                document.getElementById('dependentName').value = selectedOption.getAttribute('data-name') || '';
                document.getElementById('dependentDob').value = selectedOption.getAttribute('data-dob') || '';
                document.getElementById('dependentIdNumber').value = selectedOption.getAttribute('data-idnumber') || '';
                document.getElementById('relationship').value = selectedOption.getAttribute('data-relationship') || '';
            }
        }
    });

    function toggleDependentFields() {
        var changeType = document.getElementById('changeType').value;
        var selectorGroup = document.getElementById('dependentSelectorGroup');
        var depSelect = document.getElementById('dependentId');
        var detailFields = document.querySelectorAll('.dependent-detail-field');
        var statusField = document.getElementById('dependentStatus');
        
        // Detail inputs
        var nameInput = document.getElementById('dependentName');
        var dobInput = document.getElementById('dependentDob');
        var idNumInput = document.getElementById('dependentIdNumber');
        var relInput = document.getElementById('relationship');
        var fileInput = document.getElementById('dependentFile');

        if (changeType === 'ADD') {
            selectorGroup.style.display = 'none';
            depSelect.required = false;
            depSelect.value = '';
            
            statusField.value = 'Active';

            detailFields.forEach(function(field) {
                field.style.display = 'block';
            });
            
            nameInput.required = true;
            dobInput.required = true;
            idNumInput.required = true;
            relInput.required = true;
            fileInput.required = true;
            
            // Clear fields
            nameInput.value = '';
            dobInput.value = '';
            idNumInput.value = '';
            relInput.value = '';
            fileInput.value = '';

        } else if (changeType === 'UPDATE') {
            selectorGroup.style.display = 'block';
            depSelect.required = true;
            
            statusField.value = 'Active';

            detailFields.forEach(function(field) {
                field.style.display = 'block';
            });
            
            nameInput.required = true;
            dobInput.required = true;
            idNumInput.required = true;
            relInput.required = true;
            fileInput.required = true;

            // Trigger details fill if a dependent was already selected
            if (depSelect.value) {
                depSelect.dispatchEvent(new Event('change'));
            } else {
                nameInput.value = '';
                dobInput.value = '';
                idNumInput.value = '';
                relInput.value = '';
                fileInput.value = '';
            }

        } else if (changeType === 'REMOVE') {
            selectorGroup.style.display = 'block';
            depSelect.required = true;
            
            statusField.value = 'Inactive';

            detailFields.forEach(function(field) {
                field.style.display = 'none';
            });
            
            nameInput.required = false;
            dobInput.required = false;
            idNumInput.required = false;
            relInput.required = false;
            fileInput.required = false;
            
            // Clear inputs
            nameInput.value = '';
            dobInput.value = '';
            idNumInput.value = '';
            relInput.value = '';
            fileInput.value = '';
        }
    }

    ClassicEditor
        .create(document.querySelector('#reasonEditor'), {
            toolbar: ['heading', '|', 'bold', 'italic', 'underline', 'link', 'bulletedList', 'numberedList', '|', 'alignment', 'fontColor', 'fontFamily', 'fontSize', '|', 'insertTable', 'blockQuote', 'undo', 'redo']
        })
        .catch(error => { console.error(error); });

    // Initialize state on load
    toggleDependentFields();
</script>
