function translit(str) {
    str = str.toLowerCase().replace(/<.+>/, ' ').replace(/\s+/, ' ');
    var c = {
        'а':'a', 'б':'b', 'в':'v', 'г':'g', 'д':'d', 'е':'e', 'ё':'jo', 'ж':'zh', 'з':'z', 'и':'i', 'й':'j', 'к':'k', 'л':'l',
        'м':'m', 'н':'n', 'о':'o', 'п':'p', 'р':'r', 'с':'s', 'т':'t', 'у':'u', 'ф':'f', 'х':'h', 'ц':'c', 'ч':'ch', 'ш':'sh',
        'щ':'shch', 'ъ':'', 'ы':'y', 'ь':'', 'э':'e', 'ю':'ju', 'я':'ja', ' ':'-', ';':'', ':':'', ',':'', '—':'-', '–':'-',
        '.':'', '«':'', '»':'', '"':'', "'":'', '@':''
    };
    var newStr = new String();
    for (var i = 0; i < str.length; i++) {
        ch = str.charAt(i);
        newStr += ch in c ? c[ch] : ch;
    }
    return newStr;
}

