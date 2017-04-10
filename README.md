# CommonAdapter
v2.0.1
A generic RecyclerView adapter package
### 1) In your root build.gradle:
allprojects {

    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
### 2) In your library/build.gradle add:
<pre><code>
compile 'com.github.icuihai.CommonAdapter:app:v2.0.1'
</code></pre>
## Usage 
<pre><code> adapter = new GCommonRVAdapter<<T>>(this, R.layout.item_rv, list) {
            @Override
            public void convert(GViewHolder gViewHolder, T bean, int position) {
                gViewHolder.setText(R.id.tv, list.get(position).toString());
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
</code></pre>
* 1,T is you Entity class
* 2,{}==<>
## About Me
weibo : <https://www.weibo.com/icuihai>

gmail  : icuihai@gmail.com
## License
Copyright 2017 icuihai.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

