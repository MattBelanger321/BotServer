import discord
from pip._vendor import requests

client = discord.Client()
special = '~'


@client.event
async def on_message(message):
    print(message.content)
    if message.author.bot:
        return

    x = message.content.split()  # arg array
    if message.content.startswith(special + 'test'):
        await message.channel.send('I\'m Alive MotherFucker')
        return
    elif message.content.startswith(special + 'help'):
        await message.channel.send(
            'AVAILABLE COMMANDS: \n' +
            '1: ' + special + 'help\n' +
            '2: ' + special + 'me\n' +
            '3: ' + special + 'CSSPROP <property name>\n'
            '4: ' + special + 'CDOCS <function name>\n' +
            '5: ' + special + 'LINCOM  <command name>\n' +
            '6: ' + special + 'HTML  <tag name> (no brackets)\n' +
            'And that\'s all you get'
        )
    elif message.content.startswith(special + 'CDOCS'):

        if len(x) != 2:
            await message.channel.send('USAGE: ' + special + 'CDOCS <function name>')
            return

        if await printURL('https://man7.org/linux/man-pages/man2/' + x[1] + '.2.html', message) != 0:
            return
        elif await printURL('https://man7.org/linux/man-pages/man3/' + x[1] + '.3.html', message) != 0:
            return
        else:
            await message.channel.send('ERROR 404: NO DOCS FOUND')
            return

    elif message.content.startswith(special + 'me'):
        await message.channel.send('https://github.com/MattBelanger321/DocuBot')
        return
    elif message.content.startswith(special + 'LINCOM'):
        if len(x) != 2:
            await message.channel.send('USAGE: ' + special + 'LINCOM <command name>')
            return

        if await printURL('https://man7.org/linux/man-pages/man1/' + x[1] + '.1.html', message) != 0:
            return
        elif await printURL('https://man7.org/linux/man-pages/man1/' + x[1] + '.1p.html', message) != 0:
            return
        elif await printURL('https://man7.org/linux/man-pages/man1/' + x[1] + '.1x.html', message) != 0:
            return
        else:
            await message.channel.send('ERROR 404: NO DOCS FOUND')
    elif message.content.startswith(special + 'HTML'):
        if len(x) != 2:
            await message.channel.send('USAGE: ' + special + 'HTML <tag> (no brackets)')
            return

        if await printURL('https://www.w3schools.com/tags/tag_' + x[1] + '.asp', message) != 0:
            return
        else:
            await message.channel.send('ERROR 404: NO DOCS FOUND')
    elif message.content.startswith(special + 'CSSPROP'):
        if len(x) != 2:
            await message.channel.send('USAGE: ' + special + 'CSS <property name>')
            return

        if await printURL('https://www.w3schools.com/cssref/css3_pr_' + x[1] + '.asp', message) != 0:
            return
        else:
            await message.channel.send('ERROR 404: NO DOCS FOUND')


@client.event
async def on_ready():
    print(client.user.name)


async def printURL(URL, message) -> int:
    r = requests.get(URL)
    if r.status_code != 404:
        await message.channel.send(URL)
        return 1
    else:
        return 0


file = open('config.txt')
client.run(file.read())
