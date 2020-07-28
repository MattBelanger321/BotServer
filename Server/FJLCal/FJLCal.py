from datetime import datetime

import discord

client = discord.Client()
SPECIAL = '%'
BOTNAME = 'FakeBot'
eves = []


class Event:
    STATE_OVER = 0

    def __init__(self, name, eventDate):
        self.name = name
        self.date = eventDate
        self.state = -1

    async def time_left(self, channel):
        now = datetime.now()
        if self.date < now:
            await channel.send('Date Already Passed\n')
            return

        timeLeft = str(self.date - now).split()

        if len(timeLeft) == 3:
            smartTime = timeLeft[0] + ' Days'
        else:
            timeLeft = timeLeft[0].split(':')
            if timeLeft[0] == '0':
                if timeLeft[1] == '00':
                    smartTime = 'Less than a Minute'
                else:
                    smartTime = timeLeft[1] + ' Minutes'
            else:
                smartTime = timeLeft[0] + ' Hours and ' + timeLeft[1] + ' Minutes'
        await channel.send(self.name + ' is in: **' + smartTime + '**\n')


    #TODO
    def set_alarm(self):
        now = datetime.now()
        if self.date < now:
            print('Date Already Passed\n')
            return

        timeLeft = str(self.date - now).split()

        if len(timeLeft) == 3:
            smartTime = timeLeft[0] + ' Days'
        else:
            timeLeft = timeLeft[0].split(':')
            if timeLeft[0] == '0':
                if timeLeft[1] == '00':
                    smartTime = 'Less than a Minute'
                else:
                    smartTime = timeLeft[1] + ' Minutes'
            else:
                smartTime = timeLeft[0] + ' Hours and ' + timeLeft[1] + ' Minutes'
        print(self.name + ' is in: **' + smartTime + '**\n')


def getEvents():
    f = open('cal.txt','r')
    file = f.read()

    if len(file) == 0:
        return

    while 1:
        try:
            line = file[0:file.index('!')]
            name = line[line.index('\'')+1:line.rindex('\'')]
            date = line[line.rindex('\'')+2:].split('=')
        except ValueError:
            break
        eves.append(Event(name,datetime(int(date[0]),int(date[1]),int(date[2]),int(date[3]),int(date[4]),int(date[5]))))
        file = file[file.index('!')+1:]
    f.close()


def updateCalendar():
    file = open('cal.txt','w')
    for i in range(len(eves)):
        encrptDate = str(eves[i].date).replace('-', '=').replace(' ', '=').replace(':', '=')
        file.write("'"+eves[i].name+"' "+encrptDate+"!\n")
    file.close()


@client.event
async def on_message(message):
    if message.author.bot:
        return

    x = message.content.split(' ')

    if message.content.startswith(SPECIAL + 'help') | message.content.startswith('@' + BOTNAME):
        await message.channel.send(
            'Available Commands\n' +
            '1: ' + SPECIAL + 'help\n' +
            '2: ' + SPECIAL +'me\n'+
            '3: ' + SPECIAL +'addEvent\n'+
            'And thats about it champ'
        )
    elif message.content.startswith(SPECIAL + 'me'):
        await message.channel.send('https://github.com/MattBelanger321/DiscordCalendar')
    elif message.content.startswith(SPECIAL + 'addEvent'):
        if len(x) != 7:
            await message.channel.send('USAGE: ' + SPECIAL + 'addEvent eventName YYYY MM DD hh mm')
            return
        new = Event(x[1], datetime(int(x[2]), int(x[3]), int(x[4]), int(x[5]), int(x[6])))
        await new.time_left(message.channel)
        eves.append(new)
        updateCalendar()


@client.event
async def on_ready():
    print(client.user.name)
    getEvents()


file = open('config.txt')
client.run(file.read())
