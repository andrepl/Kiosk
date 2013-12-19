slime = [{"text":"████████","color":"green"},
{"text":"████████","color":"green"},
{"text":"","extra":[{"text":"█","color":"green"},{"text":"██","color":"dark_green"},{"text":"██","color":"green"},{"text":"██","color":"dark_green"},{"text":"█","color":"green"}]},
{"text":"","extra":[{"text":"█","color":"green"},{"text":"██","color":"dark_green"},{"text":"██","color":"green"},{"text":"██","color":"dark_green"},{"text":"█","color":"green"}]},
{"text":"████████","color":"green"},
{"text":"","extra":[{"text":"████","color":"green"},{"text":"█","color":"dark_green"},{"text":"███","color":"green"}]},
{"text":"████████","color":"green"},
{"text":"████████","color":"green"}]

COLORS = dict(black=0, )
def toMC(obj):
    if 'color' in obj:
        line = COLORS[obj['color']]
    for row in obj:
        line = "§" + COLORS[row['color']]
        line += row['text']