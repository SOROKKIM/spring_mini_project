let targetId;

$(document).ready(function () {
    // cookie 여부 확인하여 로그인 확인
    const auth = getToken();

    if(auth !== '') {
        $('#username').text('수강생');
        $('#login-true').show();
        $('#login-false').hide();
    } else {
        $('#login-false').show();
        $('#login-true').hide();
    }
})

function logout() {
    // 토큰 값 ''으로 덮어쓰기
    document.cookie =
        'Authorization' + '=' + '' + ';path=/';
    window.location.reload();
}

function  getToken() {
    let cName = 'Authorization' + '=';
    let cookieData = document.cookie;
    let cookie = cookieData.indexOf('Authorization');
    let auth = '';
    if(cookie !== -1){
        cookie += cName.length;
        let end = cookieData.indexOf(';', cookie);
        if(end === -1)end = cookieData.length;
        auth = cookieData.substring(cookie, end);
    }
    return auth;
}


$(document).ready(function () {
    // HTML 문서를 로드할 때마다 실행합니다.
    getMessages();
})

// 메모를 불러와서 보여줍니다.
function getMessages() {
    // 1. 기존 메모 내용을 지웁니다.
    $('#cards-box').empty();

    // 2. 메모 목록을 불러와서 HTML로 붙입니다.
    $.ajax({
        type: "GET",
        url: "/api/memos",
        data: {},
        success: function (response) {
            for (let i = 0; i < response.length; i++) {
                let memo = response[i];
                let id = memo['id'];
                let username = memo['username'];
                let title = memo['title'];
                let password = memo['password'];
                let contents = memo['contents'];
                let modifiedAt = memo['modifiedAt'];
                addHTML(id, username, title, password, contents, modifiedAt);
            }
        }
    });
}

// 메모 하나를 HTML로 만들어서 body 태그 내 원하는 곳에 붙입니다.
function addHTML(id, username, title, password, contents, modifiedAt) {
    let tempHtml = makeMessage(id, username, title, password, contents, modifiedAt);
    $('#cards-box').append(tempHtml);
}

function makeMessage(id, username, title, password, contents, modifiedAt) {
    return `<div class="card" style="width: 18rem;">
                      <div class="card-body">
                        <!-- title 영역 -->
                        <div class="titledata" id="${id}-title">
                            <h5 class="card-title">${title}</h5>
                        </div>
                        <!-- date/username 영역 -->
                        <div class="metadata">
                            <div id="${id}-username" class="username">
                                <h6 class="card-subtitle mb-2 text-muted">${username}</h6>
                            </div>
                            <div class="date">
                                <h6 class="card-subtitle mb-2 text-muted">${modifiedAt}</h6>
                            </div>
                        </div>
                        <!-- contents 조회/수정 영역-->
                        <div class="editpart">
                            <div class="contents">
                                <div id="${id}-contents" class="text">
                                    ${contents}
                                </div>
                                <div id="${id}-editarea" class="edit">
                                    <textarea id="${id}-textarea" class="te-edit" name="" cols="30" rows="5" style="display: none"></textarea>
                                </div>
                            </div>
                            <!-- 버튼 영역-->
                            <div class="footer">
                                <div class="mb-3">
                                    <input type="password" class="form-control post-detail-password" id="${id}-inputPassword" placeholder="비밀번호를 입력해주세요"
                                           name="password" style="display: none">
                                </div>
                                <img id="${id}-edit" class="icon-start-edit" src="images/edit.png" alt="" onclick="editPost('${id}')">
                                <img id="${id}-delete1" class="icon-delete" src="images/delete.png" alt="" onclick="deletePost('${id}')">
                                <img id="${id}-delete" class="icon-delete" style="display: none" src="images/delete.png" alt="" onclick="deleteOne('${id}')">
                                <img id="${id}-submit" class="icon-end-edit"style="display: none" src="images/done.png" alt="" onclick="submitEdit('${id}')">
                            </div>
                        </div>
                    </div>
                </div>`;
}

// 사용자가 내용을 올바르게 입력하였는지 확인합니다.
function isValidContents(contents) {
    if (contents == '') {
        alert('내용을 입력해주세요');
        return false;
    }
    if (contents.trim().length > 250) {
        alert('공백 포함 250자 이하로 입력해주세요');
        return false;
    }
    return true;
}

// 메모를 생성합니다.
function writePost() {
    // 1. 작성한 메모를 불러옵니다.
    let contents = $('#contents').val();

    // 2. 작성한 메모가 올바른지 isValidContents 함수를 통해 확인합니다.
    if (isValidContents(contents) == false) {
        return;
    }

    // 3. username을 불러옵니다.
    let username = $('#inputUsername').val();

    // 제목을 불러옵니다.
    let title = $('#inputTitle').val();

    //비밀번호를 불러옵니다.
    let password = $('#inputPassword').val();

    // 4. 전달할 data JSON으로 만듭니다.
    let data = {'username': username, 'title': title, 'password': password, 'contents': contents};

    $.ajax({
        type: "POST",
        url: "/api/memos",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            alert('게시글이 성공적으로 작성되었습니다.');
            window.location.reload();
        }
    });
}

// 수정 버튼을 눌렀을 때, 기존 작성 내용을 textarea 에 전달합니다.
// 숨길 버튼을 숨기고, 나타낼 버튼을 나타냅니다.
function editPost(id) {
    showEdits(id);
    let contents = $(`#${id}-contents`).text().trim();
    $(`#${id}-textarea`).val(contents);
}

function showEdits(id) {
    $(`#${id}-textarea`).show();
    $(`#${id}-submit`).show();
    $(`#${id}-delete1`).hide();
    $(`#${id}-inputPassword`).show();

    $(`#${id}-contents`).show();
    $(`#${id}-edit`).hide();
}

// 메모를 수정합니다.
function submitEdit(id) {
    // 1. 작성 대상 메모의 username과 contents 를 확인합니다.
    let username = $(`#${id}-username`).text().trim();
    let contents = $(`#${id}-textarea`).val().trim();
    let password = $(`#${id}-inputPassword`).val().trim();
    if (isValidContents(contents) === false) {
        return;
    }
    let data = {'username': username, 'password': password, 'contents': contents};

    // 4. PUT /api/memos/{id} 에 data를 전달합니다.
    $.ajax({
        type: "PUT",
        url: `/api/memos/${id}`,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            if (response === 0) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            } else {
                alert('내용 변경이 완료되었습니다.');
                window.location.reload();
            }
        }
    });
}

function deletePost(id) {
    showPassword(id);
    let contents = $(`#${id}-contents`).text().trim();
    $(`#${id}-textarea`).val(contents);
}
function showPassword(id) {
    $(`#${id}-textarea`).hide();
    $(`#${id}-submit`).hide();
    $(`#${id}-delete`).show();
    $(`#${id}-inputPassword`).show();
    $(`#${id}-delete1`).hide();


    $(`#${id}-contents`).show();
    $(`#${id}-edit`).hide();
}

function deleteOne(id) {
    let password = $(`#${id}-inputPassword`).val().trim();
    let data = {'password': password};
    $.ajax({
        type: "DELETE",
        url: `/api/memos/${id}`,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            if (response === 0) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            } else {
                alert('메시지 삭제에 성공하였습니다.');
                window.location.reload();
            }
        }
    })
}

function open_box() {
    $('#post-box').show()
}

function close_box() {
    $('#post-box').hide()
}


