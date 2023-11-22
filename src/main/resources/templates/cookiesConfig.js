console.log('config');
function readCookies() {
    const cookies = document.cookie.split(';');; // This gets all cookies as a single string
    let cookie_user = '';
    for (const cookie of cookies) {
        const parts = cookie.split('=');
        if (parts[0].trim() === 'log') {
            cookie_user = parts[1];
            break;
        }
    }
    return convertToObject(cookie_user);
}

function convertToObject(str) {
    const obj = {};
    const keyValuePairs = str.split('|');

    keyValuePairs.forEach(pair => {
        const [key, value] = pair.split(':');
        obj[key] = isNaN(Number(value)) ? value : Number(value);
    });

    return obj;
}

function updateLoginLogoutDisplay() {
    const objCookie = readCookies();
    // Assuming 'log' is the name of the cookie that holds the login status
    const isLoggedIn = objCookie && objCookie.login;

    // Find the login/logout list item
    const loginListItem = $('.nav-item').filter(function() {
        return $(this).find('a').text().toUpperCase().includes('LOG IN');
    });

    // Update the text and href based on login status
    if (isLoggedIn) {
        loginListItem.find('a').text('Hallo ' + objCookie.login + ', LOG OUT');
        loginListItem.find('a').attr('href', '/logout'); // Set this to your actual logout URL
    } else {
        loginListItem.find('a').text('LOG IN');
        loginListItem.find('a').attr('href', '/login');
    }
}

// Call updateLoginLogoutDisplay on page load to set the correct option
updateLoginLogoutDisplay();