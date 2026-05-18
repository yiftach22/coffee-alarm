# Coffee Alarm

An Android alarm app prototype with a twist: to stop snoozing, you have to point your phone camera at an actual cup of coffee. The idea — get yourself out of bed and as far as the coffee machine before you can silence the alarm.

Built as a personal project to apply ML, image processing and Android concepts from my CS coursework at Hebrew University.

## How it works

- **Alarm flow:** Standard Android alarm with snooze. The "stop" action is replaced by a camera check — the alarm only turns off once the app's CV model confirms it's looking at a cup of coffee.
- **Data:** I collected and labeled my own dataset of coffee cup photos.
- **Model:** Fine-tuned an SSD MobileNet V2 object detector in TensorFlow, using the TensorFlow Object Detection API.
- **Deployment:** Exported the model to TFLite for on-device, real-time inference — no server, no internet required.
- **App:** Kotlin Android app that runs the model on live camera frames.

## Status: Prototype, not production

I stopped development at the prototype stage after concluding the model couldn't reach the accuracy needed for a real product. The core issue was dataset size: coffee cups have huge real-world variance (cup shape, lighting, reflections on the liquid surface, latte vs. espresso vs. black coffee, mugs vs. paper cups, partial occlusion) and my hand-collected dataset wasn't large or diverse enough to cover it.

To get to a working app I would have needed either (a) a much larger labeled dataset, (b) a pretrained model fine-tuned on coffee imagery, or (c) a narrower problem definition (e.g. only one cup type).

I kept the project public because the pipeline — data collection → training → TFLite export → Android integration — is the part I'm proud of, even though the end result didn't reach product quality.

## Tech

TensorFlow, TFLite, Kotlin, Android
