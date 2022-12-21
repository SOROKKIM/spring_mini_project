let targetId;

$(document).ready(function () {

    // cookie 여부 확인하여 로그인 확인
    const auth = getToken();

    if (auth !== '') {
        $('#username').text('username');
        $('#login-true').show();
        $('#login-false').hide();
    } else {
        $('#login-false').show();
        $('#login-true').hide();
    }
    getMessages();
})

function logout() {
    // 토큰 값 ''으로 덮어쓰기
    document.cookie =
        'Authorization' + '=' + '' + ';path=/';
    window.location.reload();
}

function getToken() {
    let cName = 'Authorization' + '=';
    let cookieData = document.cookie;
    let cookie = cookieData.indexOf('Authorization');
    let auth = '';
    if (cookie !== -1) {
        cookie += cName.length;
        let end = cookieData.indexOf(';', cookie);
        if (end === -1) end = cookieData.length;
        auth = cookieData.substring(cookie, end);
    }
    return auth;
}


// 메모를 불러와서 보여줍니다.
function getMessages() {
    /**
     * 메모 목록: #cards-box
     * 메모 HTML 만드는 함수: addHTML
     */

    const auth = getToken();

    // 1. GET /api/products 요청
    $.ajax({
        type: 'GET',
        url: '/api/memos',
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", auth);
        },
        success: function (response) {

            // 3. for 문마다 관심 상품 HTML 만들어서 관심상품 목록에 붙이기!
            for (let i = 0; i < response.length; i++) {
                let memo = response[i];
                let tempHtml = addHTML(memo);
                $('#cards-box').append(tempHtml);
            }
        }
    })
}

// 메모 하나를 HTML로 만들어서 body 태그 내 원하는 곳에 붙입니다.
function addHTML(id, username, title, contents, modifiedAt) {
    let tempHtml = makeMessage(id, username, title, contents, modifiedAt);
    $('#cards-box').append(tempHtml);
}

function makeMessage(memo) {
    return `<div class="card" style="width: 18rem;">
                      <div class="card-body">
                        <!-- title 영역 -->
                        <div class="titledata" id="${memo.id}-title">
                            <h5 class="card-title">${memo.title}</h5>
                        </div>
                        <!-- date/username 영역 -->
                        <div class="metadata">
                            <div id="${memo.id}-username" class="username">
                                <h6 class="card-subtitle mb-2 text-muted">${memo.username}</h6>
                            </div>
                            <div class="date">
                                <h6 class="card-subtitle mb-2 text-muted">${memo.modifiedAt}</h6>
                            </div>
                        </div>
                        <!-- contents 조회/수정 영역-->
                        <div class="editpart">
                            <div class="contents">
                                <div id="${memo.id}-contents" class="text">
                                    ${memo.contents}
                                </div>
                                <div id="${memo.id}-editarea" class="edit">
                                    <textarea id="${memo.id}-textarea" class="te-edit" name="" cols="30" rows="5" style="display: none"></textarea>
                                </div>
                            </div>
                            <!-- 버튼 영역-->
                            <div class="footer">
                                <img id="${memo.id}-edit" class="icon-start-edit" src="/images/edit.png" alt="" onclick="editPost('${memo.id}')">
                                <img id="${memo.id}-delete" class="icon-delete"  src="/images/delete.png" alt="" onclick="deleteOne('${memo.id}')">
                                <img id="${memo.id}-submit" class="icon-end-edit"style="display: none" src="/images/done.png" alt="" onclick="submitEdit('${memo.id}')">
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
    /**
     * modal 뜨게 하는 법: $('#container').addClass('active');
     * data를 ajax로 전달할 때는 두 가지가 매우 중요
     * 1. contentType: "application/json",
     * 2. data: JSON.stringify(itemDto),
     */
    let contents = $('#contents').val();
    if (isValidContents(contents) == false) {
        return;
    }

    const auth = getToken();

    let title = $('#inputTitle').val();
    let data = {'title': title, 'contents': contents};

    // 1. POST /api/memos 에 메모 생성 요청
    $.ajax({
        type: "POST",
        url: `/api/memos`,
        contentType: "application/json",
        data: JSON.stringify(data),
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", auth);
        },
        success: function (response) {
            // 2. 응답 함수에서 modal을 뜨게 하고, targetId 를 reponse.id 로 설정
            $('#container').addClass('active');
            targetId = response.id;

            alert('메시지가 성공적으로 작성되었습니다.');
            window.location.reload();
        }
    })
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

function deleteOne(id) {

    // let loginUser = [[${username}]];
    // if (loginUser !== username) {
    //     alert("글쓴이만 삭제 가능합니다.");
    //     return;
    // }
    const auth = getToken();
    // let loginUser = $(`#${id}-username`).val().trim();
    // let data = {'username': loginUser};

    $.ajax({
        type: "DELETE",
        url: `/api/memos/${id}`,
        contentType: "application/json",
        // data: JSON.stringify(data),

        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", auth);
        },
        success: function (response) {
            alert('메시지 삭제에 성공하였습니다.');
            window.location.reload();
        }
    })

}


function open_box() {
    $('#post-box').show()
}

function close_box() {
    $('#post-box').hide()
}


