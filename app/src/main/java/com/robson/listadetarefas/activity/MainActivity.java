package com.robson.listadetarefas.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robson.listadetarefas.R;
import com.robson.listadetarefas.adapter.TarefaAdapter;
import com.robson.listadetarefas.base_de_dados.DbHelper;
import com.robson.listadetarefas.dao.TarefaDAO;
import com.robson.listadetarefas.helper.RecyclerItemClickListener;
import com.robson.listadetarefas.model.Tarefa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //criando variavel para ultilizar o recyclerView
    private RecyclerView recyclerView;

    private TarefaAdapter tarefaAdapter;

    private List<Tarefa> listaTarefa = new ArrayList<>();
    private Tarefa tarefaSelecionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //parte superior da tela
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //configurar recyclerView
        recyclerView = findViewById(R.id.recyclerView);
        //instacia da classe
        DbHelper db = new DbHelper(getApplicationContext());
        //montando a estrutura para salvar na base de dados
        ContentValues contentValues = new ContentValues();
        contentValues.put("nome","teste");
        //para salvar na base de dados
        db.getWritableDatabase().insert("tarefas",null, contentValues);

       //Adicionar evento de clique da classe RecyclerItemClickListener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Recuperar tarefa para edicao
                Tarefa tarefaSelecionada = listaTarefa.get( position );

                //Envia tarefa para tela adicionar tarefa
                Intent i = new Intent(MainActivity.this, AdicionarTarefaActivity.class);
                i.putExtra("tarefaSelecionada", tarefaSelecionada );

                startActivity( i );

            }

            @Override
            public void onLongItemClick(View view, int position) {
//Recupera tarefa para deletar
                tarefaSelecionada = listaTarefa.get( position );

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                //Configura título e mensagem
                dialog.setTitle("Confirmar exclusão");
                dialog.setMessage("Deseja excluir a tarefa: " + tarefaSelecionada.getNome() + " ?" );

                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
                        if ( tarefaDAO.deletar(tarefaSelecionada) ){

                            carregarTarefas();
                            Toast.makeText(getApplicationContext(),
                                    "Sucesso ao excluir tarefa!",
                                    Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(getApplicationContext(),
                                    "Erro ao excluir tarefa!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.setNegativeButton("Não", null );

                //Exibir dialog
                dialog.create();
                dialog.show();

            }



            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }

        ));

        //botao adicionar
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chamando a intent adicionar tarefa  dentro do botão adicionar
                Intent i = new Intent(getApplicationContext(), AdicionarTarefaActivity.class);
                startActivity(i);
            }
        });
    }
    public void carregarTarefas()
    {
        //configurando uma lista puxando da base
                                            //passando o context como parametro
        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
        //adicionando ao dados a variavel
        listaTarefa =  tarefaDAO.listar();

        //configurando uma lista statica
        // Tarefa tarefa1 = new Tarefa();
        // tarefa1.setNome("teste");
        //listaTarefa.add(tarefa1);

        //Tarefa tarefa2 = new Tarefa();
        //tarefa2.setNome("teste1");
        //listaTarefa.add(tarefa2);

        //Adcionando os dados ao adpter
        tarefaAdapter = new TarefaAdapter(listaTarefa);




RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
recyclerView.setLayoutManager(layoutManager);
recyclerView.setHasFixedSize(true);
recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
recyclerView.setAdapter(tarefaAdapter);
    }

    @Override
    protected void onStart() {
        carregarTarefas();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
